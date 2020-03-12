package benchmark

import java.io.*
import java.nio.*
import java.nio.charset.*

// Singleton object lazy initializes on the first use, internal for tests
internal object LineReader6 {
    private const val BUFFER_SIZE: Int = 32
    private lateinit var decoder: CharsetDecoder
    private var directEOL = false
    private val bytes = ByteArray(BUFFER_SIZE)
    private val chars = CharArray(BUFFER_SIZE)
    private val byteBuf: ByteBuffer = ByteBuffer.wrap(bytes)
    private val charBuf: CharBuffer = CharBuffer.wrap(chars)
    private val sb = StringBuilder()

    /**
     * Reads line from the specified [inputStream] with the given [charset].
     * The general design:
     * * This function contains only fast path code and all it state is kept in local variables as much as possible.
     * * All the slow-path code is moved to separate functions and the call-sequence bytecode is minimized for it.
     */
    @Synchronized
    fun readLine(inputStream: InputStream, charset: Charset): String? { // charset == null -> use default
        if (!::decoder.isInitialized || decoder.charset() != charset) updateCharset(charset)
        val decoder = decoder
        val directEOL = directEOL
        val bytes = bytes
        val chars = chars
        val byteBuf = byteBuf
        val charBuf = charBuf
        val sb = sb
        var nBytes = 0
        var nChars = 0
        while (true) {
            val readByte = inputStream.read()
            if (readByte == -1) {
                // The result is null only if there was absolutely nothing read
                if (sb.isEmpty() && nBytes == 0 && nChars == 0) {
                    return null
                } else {
                    nChars = decodeEndOfInput(nBytes, nChars) // throws exception if partial char
                    break
                }
            } else {
                bytes[nBytes++] = readByte.toByte()
            }
            // With "directEOL" encoding bytes are batched before being decoded all at once
            if (readByte == '\n'.toInt() || nBytes == BUFFER_SIZE || !directEOL) {
                // Decode the bytes that were read
                byteBuf.limit(nBytes) // byteBuf position is always zero
                charBuf.position(nChars) // charBuf limit is always BUFFER_SIZE
                nChars = decode(false, decoder, byteBuf, chars, charBuf, sb)
                // Break when we have decoded end of line
                if (nChars > 0 && chars[nChars - 1] == '\n') {
                    byteBuf.position(0) // reset position for next use
                    break
                }
                // otherwise we're going to read more bytes, so compact byteBuf
                nBytes = compactBytes()
            }
        }
        // Trim the end of line
        if (nChars > 0 && chars[nChars - 1] == '\n') {
            nChars--
            if (nChars > 0 && chars[nChars - 1] == '\r') nChars--
        }
        // Fast path for short lines (don't use StringBuilder)
        if (sb.isEmpty()) return String(chars, 0, nChars)
        // Copy the rest of chars to StringBuilder
        sb.append(chars, 0, nChars)
        // Build the result
        val result = sb.toString()
        if (sb.length > BUFFER_SIZE) trimStringBuilder()
        sb.setLength(0)
        return result
    }

    // The result is the number of chars in charBuf
    private fun decode(
        endOfInput: Boolean,
        decoder: CharsetDecoder, byteBuf: ByteBuffer,
        chars: CharArray, charBuf: CharBuffer, sb: StringBuilder
    ): Int {
        while (true) {
            val coderResult: CoderResult = decoder.decode(byteBuf, charBuf, endOfInput)
            if (coderResult.isError) {
                resetAll() // so that next call to readLine starts from clean state
                coderResult.throwException()
            }
            val nChars = charBuf.position()
            if (!coderResult.isOverflow) return nChars // has room in buffer -- everything possible was decoded
            // overflow (charBuf is full) -- offload everything from charBuf but last char into sb
            sb.append(chars, 0, nChars - 1)
            charBuf.position(0)
            charBuf.limit(BUFFER_SIZE)
            charBuf.put(chars[nChars - 1]) // retain last char
        }
    }

    // Slow path -- only on long lines (extra call to decode will be performed)
    private fun compactBytes(): Int = with(byteBuf) {
        compact()
        return position().also { position(0) }
    }

    // Slow path -- only on end of input
    private fun decodeEndOfInput(nBytes: Int, nChars: Int): Int {
        byteBuf.limit(nBytes) // byteBuf position is always zero
        charBuf.position(nChars) // charBuf limit is always BUFFER_SIZE
        return decode(true, decoder, byteBuf, chars, charBuf, sb).also { // throws exception if partial char
            // reset decoder and byteBuf for next use
            decoder.reset()
            byteBuf.position(0)
        }
    }

    // Slow path -- only on charset change
    private fun updateCharset(charset: Charset) {
        decoder = charset.newDecoder()
        // try decoding ASCII line separator to see if this charset (like UTF-8) encodes it directly
        byteBuf.clear()
        charBuf.clear()
        byteBuf.put('\n'.toByte())
        byteBuf.flip()
        decoder.decode(byteBuf, charBuf, false)
        directEOL = charBuf.position() == 1 && charBuf.get(0) == '\n'
        resetAll()
    }

    // Slow path -- only on exception in decoder and on charset change
    private fun resetAll() {
        decoder.reset()
        byteBuf.position(0)
        sb.setLength(0)
    }

    // Slow path -- only on long lines
    private fun trimStringBuilder() {
        sb.setLength(BUFFER_SIZE)
        sb.trimToSize()
    }
}
