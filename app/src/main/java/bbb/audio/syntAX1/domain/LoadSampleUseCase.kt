package bbb.audio.syntAX1.domain

import android.content.Context
import android.util.Log
import bbb.audio.syntAX1.data.local.entity.Sample
import bbb.audio.syntAX1.data.repository.SamplerRepository

/**
 * Use case for loading a sample into the sampler engine.
 *
 * Responsibilities:
 * - Load sample bytes from DB
 * - Pass to SamplerRepository for Pd loading
 */
class LoadSampleUseCase(
    private val samplerRepository: SamplerRepository,
    private val context: Context
) {
    /**
     * Load a sample into the sampler.
     *
     * @param sample The sample to load
     */
    suspend operator fun invoke(sample: Sample) {
        try {
            Log.d(TAG, "Loading sample: ${sample.name}")

            if (sample.audioData.isNotEmpty()) {
                val success = samplerRepository.loadLoopSample(
                    audioBytes = sample.audioData,
                    sampleName = sample.name
                )

                if (success) {
                    Log.i(TAG, "✓ Sample loaded: ${sample.name}")
                } else {
                    Log.e(TAG, "Failed to load sample")
                    throw Exception("loadLoopSample returned false")
                }
            } else {
                throw Exception("Sample has no audio data")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            throw e
        }
    }

    companion object {
        private const val TAG = "LoadSampleUseCase"
    }
}