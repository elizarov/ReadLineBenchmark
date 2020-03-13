package benchmark

import java.io.*
import java.nio.*
import java.nio.charset.*

// Singleton object lazy initializes on the first use, internal for tests
internal object LineReader7NoLV {
    private const val BUFFER_SIZE: Int = 64 // ASCII lines up to this length can be decoded super-fast
    private const val CS_ASCII = 0 // charset directly encodes all ASCII chars
    private const val CS_DIRECT_LF = 1 // charset directly encodes LF char (`\n`)
    private const val CS_OTHER = 2 // other, more complex charset

    private lateinit var decoder: CharsetDecoder
    private var csKind = 0 // one of CS_xxx
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
        var nBytes = 0
        var nChars = 0 // invariant: when any chars are in sb, then nChars > 0
        while (true) {
            val readByte = inputStream.read()
            if (readByte == -1) {
                // Enf of input is always slow path, no attempt to optimize anything here
                // The result is null only if there was absolutely nothing read
                if (nBytes == 0 && nChars == 0) {
                    return null
                } else {
                    nChars = decodeEndOfInput(nBytes, nChars) // throws exception if partial char
                    break
                }
            }
            // With "directEOL" encoding bytes are batched before being decoded all at once
            val lfByte = readByte == '\n'.toInt()
            // Check if the decoder directly supports ASCII and thus short ASCII line can be decoded super-fast
            if (lfByte && csKind == CS_ASCII && nChars == 0) {
                var asciiLine = true
                for (i in 0 until nBytes) if (bytes[i] < 0) {
                    asciiLine = false
                    break
                }
                if (asciiLine) {
                    if (nBytes > 0 && bytes[nBytes - 1] == '\r'.toByte()) nBytes-- // skip CR ('\r')
                    // Using deprecated java.lang.String constructor for maximal speed.
                    // It performs direct copy of ASCII byte array without any conversion.
                    @Suppress("DEPRECATION")
                    return java.lang.String(bytes, 0, 0, nBytes) as String
                }
            }
            // All other cases go by adding byte to buffer anyway
            bytes[nBytes++] = readByte.toByte()
            // When charset directly supports LF (like UTF_8 and many single-byte ones) we batch calls to decoder
            if (lfByte || nBytes == BUFFER_SIZE || csKind == CS_OTHER) {
                // Decode the bytes that were read
                byteBuf.limit(nBytes) // byteBuf position is always zero
                charBuf.position(nChars) // charBuf limit is always BUFFER_SIZE
                nChars = decode(false)
                // Break when we have decoded end of line
                if (nChars > 0 && chars[nChars - 1] == '\n') {
                    byteBuf.position(0) // reset position for next use
                    break
                }
                // otherwise, we're going to read more bytes, so compact byteBuf
                nBytes = compactBytes()
            }
        }
        // Trim the end of line
        if (nChars > 0 && chars[nChars - 1] == '\n') {
            nChars-- // skip LF ('\n')
            if (nChars > 0 && chars[nChars - 1] == '\r') nChars-- // skip CR ('\r')
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
    private fun decode(endOfInput: Boolean): Int {
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
        return decode(true).also { // throws exception if partial char
            // reset decoder and byteBuf for next use
            decoder.reset()
            byteBuf.position(0)
        }
    }

    // Slow path -- only on charset change
    private fun updateCharset(charset: Charset) {
        decoder = charset.newDecoder()
        csKind = when {
            (0..127).all { isDirectChar(it.toChar()) } -> CS_ASCII
            isDirectChar('\n') -> CS_DIRECT_LF
            else -> CS_OTHER
        }
    }

    private fun isDirectChar(ch: Char): Boolean {
        bytes[0] = ch.toByte()
        byteBuf.limit(1)
        val coderResult: CoderResult = decoder.decode(byteBuf, charBuf, false)
        return (coderResult.isUnderflow && charBuf.position() == 1 && chars[0] == ch).also {
            resetAll()
        }
    }

    // Slow path -- only on exception in decoder and on charset change
    private fun resetAll() {
        decoder.reset()
        byteBuf.clear()
        charBuf.clear()
        sb.setLength(0)
    }

    // Slow path -- only on long lines
    private fun trimStringBuilder() {
        sb.setLength(BUFFER_SIZE)
        sb.trimToSize()
    }
}
