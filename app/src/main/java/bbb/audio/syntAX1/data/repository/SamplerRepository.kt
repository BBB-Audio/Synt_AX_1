package bbb.audio.syntAX1.data.repository

import android.util.Log
import bbb.audio.syntAX1.data.engine.WavUtils
import bbb.audio.syntAX1.ui.viewmodel.FilterType
import org.puredata.core.PdBase

/**
 * Unified repository for all sampler engine communication with Pure Data.
 *
 * Responsibilities:
 * 1. Load loop samples (bytes → Pd array)
 * 2. Control playback parameters (speed, volume)
 * 3. Manage ADSR envelope
 * 4. Manage filter parameters
 * 5. BPM-sync calculations
 *
 * This replaces both SamplerEngineRepository and SamplerAudioRepository.
 */
class SamplerRepository {

    companion object {
        private const val TAG = "SamplerRepository"
        private const val SAMPLE_RATE = 44100
        private const val STEREO_CHANNELS = 2
        private const val BEATS_PER_LOOP = 16
        private const val BUFFER_NAME = "loop_buffer"

        // Buffer size for 16 beats @ 44.1kHz = 705,600 floats
        private const val BUFFER_SIZE = 705_600
    }

    // ==================== AUDIO LOADING ====================

    /**
     * Load a loop sample from raw audio bytes into Pd array.
     *
     * Handles both raw PCM and WAV-formatted data via WavUtils.
     *
     * @param audioBytes Raw audio data (PCM or WAV)
     * @param sampleName For logging
     * @return Boolean indicating success
     */
    fun loadLoopSample(audioBytes: ByteArray, sampleName: String = "sample"): Boolean {
        return try {
            Log.d(TAG, "Loading loop sample: $sampleName (${audioBytes.size} bytes)")

            // If it's raw PCM without WAV header, WavUtils will add one
            // If it already has a header, WavUtils returns it unchanged
            val wavData = WavUtils.createWavBytes(audioBytes)

            // Extract PCM data from WAV (skip header = 44 bytes)
            val pcmData = extractPcmFromWav(wavData)

            if (pcmData.isEmpty()) {
                Log.e(TAG, "Failed to extract PCM from WAV data")
                return false
            }

            // Convert bytes to float array for Pd
            val floatArray = convertBytesToFloatArray(pcmData)

            if (floatArray.isEmpty()) {
                Log.e(TAG, "Failed to convert audio bytes to float array")
                return false
            }

            // Write to Pd array
            try {
                PdBase.writeArray(BUFFER_NAME, 0, floatArray, 0, floatArray.size)
                Log.i(TAG, "✓ Loaded '$sampleName': ${floatArray.size} samples written")
            } catch (e: Exception) {
                Log.e(TAG, "Error writing to array: ${e.message}")
                return false
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Error loading loop sample: ${e.message}", e)
            false
        }
    }


    /**
     * Extract PCM data from WAV file (skip 44-byte header).
     *
     * @param wavData Full WAV file bytes
     * @return PCM audio data only
     */
    private fun extractPcmFromWav(wavData: ByteArray): ByteArray {
        return if (wavData.size > 44) {
            wavData.copyOfRange(44, wavData.size)
        } else {
            ByteArray(0)
        }
    }

    /**
     * Convert raw PCM bytes (int16) to normalized float array [-1, 1].
     *
     * @param audioBytes Int16 PCM data
     * @return Float array normalized to [-1, 1]
     */
    private fun convertBytesToFloatArray(audioBytes: ByteArray): FloatArray {
        return try {
            val shortArray = ShortArray(audioBytes.size / 2)
            var byteIndex = 0
            var shortIndex = 0

            while (byteIndex < audioBytes.size - 1) {
                val byte1 = audioBytes[byteIndex].toInt() and 0xFF
                val byte2 = audioBytes[byteIndex + 1].toInt() and 0x7F
                val negative = (audioBytes[byteIndex + 1].toInt() and 0x80) != 0

                var value = (byte2 shl 8) or byte1
                if (negative) value = value or (0x1 shl 15)

                shortArray[shortIndex] = value.toShort()
                byteIndex += 2
                shortIndex++
            }

            FloatArray(shortArray.size) { i ->
                shortArray[i] / 32768f
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting bytes to float: ${e.message}")
            FloatArray(0)
        }
    }

    // ==================== PLAYBACK CONTROL ====================

    /**
     * Set playback speed multiplier for loop synchronization.
     *
     * @param speedMultiplier 1.0 = normal, 0.5 = half-speed, 2.0 = double
     */
    fun setLoopSpeed(speedMultiplier: Float) {
        try {
            PdBase.sendFloat("sampler_loop_speed", speedMultiplier)
            Log.d(TAG, "→ PD [sampler_loop_speed]: $speedMultiplier")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting loop speed: ${e.message}")
        }
    }

    /**
     * Set output volume of the loop.
     *
     * @param volume 0.0 (silent) to 1.0 (full volume)
     */
    fun setLoopVolume(volume: Float) {
        try {
            val clampedVolume = volume.coerceIn(0f, 1f)
            PdBase.sendFloat("sampler_loop_vol", clampedVolume)
            Log.d(TAG, "→ PD [sampler_loop_vol]: $clampedVolume")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting loop volume: ${e.message}")
        }
    }

    // ==================== PITCH & PLAYBACK (Legacy from SamplerEngineRepository) ====================

    fun setPitch(value: Float) {
        PdBase.sendFloat("sampler-pitch", value)
        Log.d(TAG, "→ PD [sampler-pitch]: $value")
    }

    fun setPlaybackSpeed(value: Float) {
        PdBase.sendFloat("sampler-speed", value)
        Log.d(TAG, "→ PD [sampler-speed]: $value")
    }

    // ==================== FILTER CONTROL ====================

    fun setCutoff(value: Float) {
        PdBase.sendFloat("sampler-filter-cutoff", value.coerceIn(0f, 1f))
        Log.d(TAG, "→ PD [sampler-filter-cutoff]: $value")
    }

    fun setResonance(value: Float) {
        PdBase.sendFloat("sampler-filter-resonance", value.coerceIn(0f, 1f))
        Log.d(TAG, "→ PD [sampler-filter-resonance]: $value")
    }

    fun setFilterType(type: FilterType) {
        val pdValue = if (type == FilterType.LOW_PASS) 0f else 1f
        PdBase.sendFloat("sampler-filter-type", pdValue)
        Log.d(TAG, "→ PD [sampler-filter-type]: $pdValue")
    }

    // ==================== ENVELOPE CONTROL ====================

    fun setAttack(value: Float) {
        PdBase.sendFloat("sampler-env-attack", value.coerceIn(0f, 1f))
        Log.d(TAG, "→ PD [sampler-env-attack]: $value")
    }

    fun setDecay(value: Float) {
        PdBase.sendFloat("sampler-env-decay", value.coerceIn(0f, 1f))
        Log.d(TAG, "→ PD [sampler-env-decay]: $value")
    }

    fun setSustain(value: Float) {
        PdBase.sendFloat("sampler-env-sustain", value.coerceIn(0f, 1f))
        Log.d(TAG, "→ PD [sampler-env-sustain]: $value")
    }

    fun setRelease(value: Float) {
        PdBase.sendFloat("sampler-env-release", value.coerceIn(0f, 1f))
        Log.d(TAG, "→ PD [sampler-env-release]: $value")
    }

    // ==================== TRIGGER (Keep for future single-shot samples) ====================

    fun triggerSample(note: Int = 60) {
        PdBase.sendFloat("sampler-play", 1.0f)
        Log.d(TAG, "Sample triggered with note: $note")
    }

    // ==================== BPM SYNCHRONIZATION ====================

    /**
     * Calculate speed multiplier to keep 16-beat loop in sync with BPM.
     *
     * If sample was recorded at 120 BPM and we're now at 100 BPM,
     * we need to slow it down proportionally.
     *
     * @param currentBpm Current playback tempo
     * @param originalBpm Original recording tempo (default: 120)
     * @return Speed multiplier to apply
     */
    fun calculateSpeedForBpm(currentBpm: Float, originalBpm: Float = 120f): Float {
        val multiplier = currentBpm / originalBpm
        Log.d(TAG, "Speed calc: $currentBpm BPM → multiplier $multiplier (original: $originalBpm)")
        return multiplier
    }

    // ==================== STATUS ====================

//    fun isLoopBufferReady(): Boolean {
//        return try {
//            val size = PdBase.arraySizeForName(BUFFER_NAME)
//            size > 0
//        } catch (e: Exception) {
//            Log.w(TAG, "Loop buffer not ready: ${e.message}")
//            false
//        }
//    }
}