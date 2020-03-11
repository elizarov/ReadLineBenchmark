package benchmark

import java.io.*
import java.nio.*
import java.nio.charset.*

// Singleton object lazy initializes on the first use, internal for tests
internal object LineReader1 {
    private const val BUFFER_SIZE: Int = 32
    private const val EOL_SIZE: Int = 2 // CRLF (two chars) at most
    private lateinit var decoder: CharsetDecoder
    private var directEOL = false
    private val byteBuffer: ByteBuffer = ByteBuffer.allocate(BUFFER_SIZE)
    private val charBuffer: CharBuffer = CharBuffer.allocate(BUFFER_SIZE)
    private val stringBuilder = StringBuilder()

    @Synchronized
    fun readLine(inputStream: InputStream, charset0: Charset?): String? { // charset == null -> use default
        var read = inputStream.read()
        if (read == -1) return null
        val charset = charset0 ?: Charset.defaultCharset() // use the specified or default charset
        if (!::decoder.isInitialized || decoder.charset() != charset) updateCharset(charset)
        byteBuffer.clear()
        charBuffer.clear()
        stringBuilder.clear()
        do {
            byteBuffer.put(read.toByte())
            // With "directEOL" encoding bytes are batched before being decoded all at once
            if (!directEOL || !byteBuffer.hasRemaining() || read == '\n'.toInt()) {
                tryDecode(false)
                if (endsWithEOL()) break
            }
            read = inputStream.read()
        } while (read != -1)
        tryDecode(true) // throws exception if not decoded bytes are left
        decoder.reset()
        with(charBuffer) {
            var length = position()
            if (length > 0 && get(length - 1) == '\n') {
                length--
                if (length > 0 && get(length - 1) == '\r') {
                    length--
                }
            }
            position(0)
            limit(length)
            stringBuilder.append(charBuffer)
        }
        val result = stringBuilder.toString()
        if (stringBuilder.length > BUFFER_SIZE) trimStringBuilder()
        return result
    }

    private fun updateCharset(charset: Charset) {
        val decoder: CharsetDecoder = charset.newDecoder()
        require(decoder.maxCharsPerByte() <= 1) { "Encodings with multiple chars per byte are not supported" }
        // Only assign decoder if the above check passes, otherwise throw on every readLine call
        this.decoder = decoder
        // try decoding ASCII line separator to see if this charset (like UTF-8) encodes it directly
        byteBuffer.clear()
        charBuffer.clear()
        byteBuffer.put('\n'.toByte())
        byteBuffer.flip()
        decoder.decode(byteBuffer, charBuffer, false)
        directEOL = charBuffer.position() == 1 && charBuffer.get(0) == '\n'
    }

    private fun tryDecode(endOfInput: Boolean) {
        while (true) {
            byteBuffer.flip()
            with(decoder.decode(byteBuffer, charBuffer, endOfInput)) {
                if (isError) throwException()
            }
            byteBuffer.compact()
            if (charBuffer.remaining() >= EOL_SIZE) break
            // offload everything from charBuffer but last EOL_SIZE chars into stringBuilder
            val chars = charBuffer.position()
            charBuffer.position(0)
            charBuffer.limit(chars - EOL_SIZE)
            stringBuilder.append(charBuffer)
            charBuffer.position(chars - EOL_SIZE)
            charBuffer.limit(chars)
            charBuffer.compact()
        }
    }

    private fun endsWithEOL(): Boolean = with(charBuffer) {
        val p = position()
        return p > 0 && get(p - 1) == '\n'
    }

    private fun trimStringBuilder() {
        stringBuilder.setLength(BUFFER_SIZE)
        stringBuilder.trimToSize()
    }
}