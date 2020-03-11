package benchmark

import java.io.*
import java.nio.*
import java.nio.charset.*

// Singleton object lazy initializes on the first use, internal for tests
internal object LineReader4 {
    private const val BUFFER_SIZE: Int = 32
    private lateinit var decoder: CharsetDecoder
    private var directEOL = false
    private val byteBuf: ByteBuffer = ByteBuffer.allocate(BUFFER_SIZE)
    private val charBuf: CharBuffer = CharBuffer.allocate(BUFFER_SIZE)
    private val sb = StringBuilder()

    @Synchronized
    fun readLine(inputStream: InputStream, charset0: Charset?): String? { // charset == null -> use default
        val charset = charset0 ?: Charset.defaultCharset() // use the specified or default charset
        if (!::decoder.isInitialized || decoder.charset() != charset) updateCharset(charset)
        val decoder = decoder
        val directEOL = directEOL
        val byteBuf = byteBuf
        val charBuf = charBuf
        val sb = sb
        byteBuf.position(0)
        byteBuf.limit(BUFFER_SIZE)
        charBuf.position(0)
        charBuf.limit(BUFFER_SIZE)
        sb.setLength(0)
        decoder.reset()
        var nChars: Int
        while (true) {
            val readByte = inputStream.read()
            if (readByte == -1) {
                // The result is null only if there was absolutely nothing read
                if (allEmpty()) {
                    return null
                } else {
                    nChars = decode(true, decoder, byteBuf, charBuf, sb) // throws exception if partial char
                    break
                }
            } else {
                byteBuf.put(readByte.toByte())
            }
            // With "directEOL" encoding bytes are batched before being decoded all at once
            if (readByte == '\n'.toInt() || !byteBuf.hasRemaining() || !directEOL) {
                // Decode the bytes that were read
                nChars = decode(false, decoder, byteBuf, charBuf, sb)
                // Break when we have decoded end of line
                if (nChars > 0 && charBuf[nChars - 1] == '\n') break
            }
        }
        // Trim the end of line
        if (nChars > 0 && charBuf[nChars - 1] == '\n') {
            nChars--
            if (nChars > 0 && charBuf[nChars - 1] == '\r') nChars--
        }
        // Copy the rest of charBuffer to stringBuilder
        charBuf.position(0)
        charBuf.limit(nChars)
        sb.append(charBuf)
        // Build the result
        val result = sb.toString()
        if (sb.length > BUFFER_SIZE) trimStringBuilder(sb)
        return result
    }

    // The result is the number of chars in charBuf
    private fun decode(
        endOfInput: Boolean,
        decoder: CharsetDecoder, byteBuf: ByteBuffer, charBuf: CharBuffer, sb: StringBuilder
    ): Int {
        while (true) {
            byteBuf.flip()
            decoder.decode(byteBuf, charBuf, endOfInput).run {
                if (isError) throwException()
            }
            byteBuf.compact()
            val nChars = charBuf.position()
            if (nChars < BUFFER_SIZE) return nChars // has room in buffer -- everything was decoded, done
            // buffer is full -- offload everything from charBuf but last char into sb
            charBuf.position(0)
            charBuf.limit(BUFFER_SIZE - 1)
            sb.append(charBuf)
            charBuf.limit(BUFFER_SIZE)
            charBuf.put(charBuf[BUFFER_SIZE - 1]) // retain last char
        }
    }

    private fun updateCharset(charset: Charset) {
        val decoder: CharsetDecoder = charset.newDecoder()
        require(decoder.maxCharsPerByte() <= 1) { "Encodings with multiple chars per byte are not supported" }
        // Only assign decoder if the above check passes, otherwise throw on every readLine call
        this.decoder = decoder
        // try decoding ASCII line separator to see if this charset (like UTF-8) encodes it directly
        byteBuf.clear()
        charBuf.clear()
        byteBuf.put('\n'.toByte())
        byteBuf.flip()
        decoder.decode(byteBuf, charBuf, false)
        directEOL = charBuf.position() == 1 && charBuf.get(0) == '\n'
    }

    private fun allEmpty() = sb.isEmpty() && byteBuf.position() == 0 && charBuf.position() == 0

    private fun trimStringBuilder(sb: StringBuilder) {
        sb.setLength(BUFFER_SIZE)
        sb.trimToSize()
    }
}