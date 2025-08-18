package coredevices.speex

import java.nio.ByteBuffer

actual class SpeexCodec actual constructor(
    private val sampleRate: Long,
    private val bitRate: Int,
    private val frameSize: Int
): AutoCloseable {
    private val speexDecBits: Long = initSpeexBits()
    private val speexDecState: Long = initDecState(sampleRate, bitRate)

    /**
     * Decode a frame of audio data.
     * @param encodedFrame The encoded frame to decode.
     * @param decodedFrame The buffer to store the decoded frame in.
     *
     */
    actual fun decodeFrame(encodedFrame: ByteArray, decodedFrame: ByteArray, hasHeaderByte: Boolean): SpeexDecodeResult {
        val decodedFrameBuf = ByteBuffer.allocateDirect(decodedFrame.size)
        val result = SpeexDecodeResult.fromInt(decode(encodedFrame, decodedFrameBuf, this.speexDecBits, this.speexDecState, hasHeaderByte))
        if (result == SpeexDecodeResult.Success) {
            decodedFrameBuf.get(decodedFrame, 0, decodedFrame.size)
        }
        return result
    }

    actual override fun close() {
        destroySpeexBits(speexDecBits)
        destroyDecState(speexDecState)
    }
    private external fun decode(encodedFrame: ByteArray, decodedFrame: ByteBuffer, speexDecBits: Long, speexDecState: Long, hasHeaderByte: Boolean): Int
    private external fun initSpeexBits(): Long
    private external fun initDecState(sampleRate: Long, bitRate: Int): Long
    private external fun destroySpeexBits(speexBits: Long)
    private external fun destroyDecState(decState: Long)

    companion object {
        // Used to load the 'speex_codec' library on application startup.
        init {
            System.loadLibrary("speex_codec")
        }
    }
}