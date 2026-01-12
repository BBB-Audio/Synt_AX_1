package bbb.audio.syntAX1.data.engine

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Utility class for WAV audio format operations.
 * Provides methods for WAV header creation, validation, and manipulation.
 */
object WavUtils {

    // Standard audio format for synthesizer applications
    private const val SAMPLE_RATE = 44100
    private const val CHANNELS = 1
    private const val BITS_PER_SAMPLE = 16
    private const val BYTES_PER_SAMPLE = BITS_PER_SAMPLE / 8
    private const val BYTE_RATE = SAMPLE_RATE * CHANNELS * BYTES_PER_SAMPLE
    private const val BLOCK_ALIGN = CHANNELS * BYTES_PER_SAMPLE

    /**
     * Checks if the given byte array contains a valid WAV header.
     *
     * @param audioData The byte array to check
     * @return `true` if the data starts with a valid WAV header, `false` otherwise
     */
    fun hasWavHeader(audioData: ByteArray): Boolean {
        if (audioData.size < 12) return false

        val riff = String(audioData.copyOfRange(0, 4))
        val wave = String(audioData.copyOfRange(8, 12))

        return riff == "RIFF" && wave == "WAVE"
    }

    /**
     * Adds a standard WAV header to raw PCM audio data.
     * Assumes the PCM data is 16-bit, mono, 44100Hz.
     *
     * @param pcmData Raw PCM audio data without header
     * @return Complete WAV file bytes with proper header
     */
    fun addWavHeader(pcmData: ByteArray): ByteArray {
        val dataSize = pcmData.size
        val fileSize = dataSize + 36  // Header size without "RIFF" and fileSize fields

        val output = ByteArrayOutputStream()

        // RIFF chunk descriptor
        output.write("RIFF".toByteArray())
        output.write(intToBytes(fileSize))
        output.write("WAVE".toByteArray())

        // fmt sub-chunk
        output.write("fmt ".toByteArray())
        output.write(intToBytes(16))  // Subchunk1Size (16 for PCM)
        output.write(shortToBytes(1)) // AudioFormat (1 = PCM)
        output.write(shortToBytes(CHANNELS.toShort()))
        output.write(intToBytes(SAMPLE_RATE))
        output.write(intToBytes(BYTE_RATE))
        output.write(shortToBytes(BLOCK_ALIGN.toShort()))
        output.write(shortToBytes(BITS_PER_SAMPLE.toShort()))

        // data sub-chunk
        output.write("data".toByteArray())
        output.write(intToBytes(dataSize))
        output.write(pcmData)

        return output.toByteArray()
    }

    /**
     * Creates complete WAV file bytes from audio data.
     * If the data already has a WAV header, it's returned unchanged.
     * Otherwise, a standard header is added.
     *
     * @param audioData Either raw PCM or already formatted WAV data
     * @return Complete WAV file bytes
     */
    fun createWavBytes(audioData: ByteArray): ByteArray {
        return if (hasWavHeader(audioData)) {
            audioData
        } else {
            addWavHeader(audioData)
        }
    }

    /**
     * Converts an integer to a little-endian byte array.
     *
     * @param value The integer value to convert
     * @return 4-byte array in little-endian order
     */
    private fun intToBytes(value: Int): ByteArray {
        return ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(value)
            .array()
    }

    /**
     * Converts a short to a little-endian byte array.
     *
     * @param value The short value to convert
     * @return 2-byte array in little-endian order
     */
    private fun shortToBytes(value: Short): ByteArray {
        return ByteBuffer.allocate(2)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putShort(value)
            .array()
    }
}