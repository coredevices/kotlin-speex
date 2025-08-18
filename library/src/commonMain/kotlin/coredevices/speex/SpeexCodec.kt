package coredevices.speex

expect class SpeexCodec(
    sampleRate: Long,
    bitRate: Int,
    frameSize: Int
): AutoCloseable {
    /**
     * Decode a frame of audio data.
     * @param encodedFrame The encoded frame to decode.
     * @param decodedFrame The buffer to store the decoded frame in.
     *
     */
    fun decodeFrame(encodedFrame: ByteArray, decodedFrame: ByteArray, hasHeaderByte: Boolean = true): SpeexDecodeResult
    override fun close()
}

enum class SpeexDecodeResult {
    Success,
    EndOfStream,
    CorruptStream;

    companion object {
        fun fromInt(value: Int) = when (value) {
            0 -> Success
            -1 -> EndOfStream
            -2 -> CorruptStream
            else -> throw IllegalArgumentException("Invalid value for SpeexDecodeResult")
        }
    }
}