package coredevices.speex

import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.io.readUByte
import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SpeexCodecTest {
    @Test
    fun testSpeexCodec() {
        val codec = SpeexCodec(
            sampleRate = 16000L,
            bitRate = 12800,
            frameSize = 320
        )

        val decodedPCMBuf = Buffer()
        SystemFileSystem.source(Path("test-data/speex-frames.bin")).buffered().use { frames ->
            val decodedFrame = ByteArray(320 * Short.SIZE_BYTES)
            while (!frames.exhausted()) {
                val size = frames.readUByte()
                val frame = frames.readByteArray(size.toInt())
                val result = codec.decodeFrame(frame, decodedFrame, hasHeaderByte = false)
                assertEquals(SpeexDecodeResult.Success, result)
                decodedPCMBuf.write(decodedFrame)
                //println("Decode: $result")
            }
        }
        codec.close()

        val decodedPCM = decodedPCMBuf.readByteArray()
        val expectedPCM = SystemFileSystem.source(Path("test-data/audio-result.pcm")).buffered().readByteArray()
        assertEquals(expectedPCM.size, decodedPCM.size, "Decoded PCM size does not match expected size")
        val decodedB64 = Base64.encode(decodedPCM)
        val expectedB64 = Base64.encode(expectedPCM)
        assertEquals(expectedB64, decodedB64, "Decoded PCM does not match expected PCM")
    }
}