package coredevices.speex

import kotlinx.cinterop.Arena
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import speex.SpeexBits
import speex.speex_bits_init
import speex.speex_bits_read_from
import speex.speex_decode
import speex.speex_decode_int
import speex.speex_decoder_destroy
import speex.speex_decoder_init
import speex.speex_wb_mode

@OptIn(ExperimentalForeignApi::class)
actual class SpeexCodec actual constructor(
    private val sampleRate: Long,
    private val bitRate: Int,
    private val frameSize: Int
) : AutoCloseable {

    private val arena = Arena()
    private val speexDecBits: SpeexBits = arena.alloc()
    init {
        speex_bits_init(speexDecBits.ptr)
    }

    private val speexDecState: COpaquePointer = speex_decoder_init(speex_wb_mode.ptr)!!

    actual fun decodeFrame(
        encodedFrame: ByteArray,
        decodedFrame: ByteArray,
        hasHeaderByte: Boolean
    ): coredevices.speex.SpeexDecodeResult {
        require(decodedFrame.size == frameSize * Short.SIZE_BYTES) {
            "Decoded frame size must be ${frameSize * Short.SIZE_BYTES} bytes, but was ${decodedFrame.size} bytes."
        }
        val offset = if (hasHeaderByte) 1 else 0
        speex_bits_read_from(speexDecBits.ptr, encodedFrame.refTo(offset), encodedFrame.size - offset)
        return SpeexDecodeResult.fromInt(
            memScoped {
                speex_decode_int(
                    speexDecState,
                    speexDecBits.ptr,
                    decodedFrame.refTo(0).getPointer(this).reinterpret(),
                )
            }
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override fun close() {
        arena.clear()
        speex_decoder_destroy(speexDecState)
    }
}