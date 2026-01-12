package bbb.audio.syntAX1.domain

import bbb.audio.syntAX1.data.repository.FreesoundRepository

/**
 * Use case for validating if a sound has a preview URL available.
 * This is used before attempting to play a preview or download the sound.
 */
class ValidatePreviewUrlUseCase(
    private val freesoundRepository: FreesoundRepository
) {
    /**
     * Checks if a sound has a preview URL available.
     *
     * @param soundId The unique identifier of the sound on Freesound
     * @param apiKey Your Freesound API key for authentication
     * @return A [Result] containing the preview URL if available,
     *         or an exception if not
     */
    suspend operator fun invoke(soundId: Int, apiKey: String): Result<String> {
        return try {
            val detail = freesoundRepository.getSoundDetail(soundId, apiKey)
            val previewUrl = detail.previews?.previewHqMp3
                ?: return Result.failure(IllegalStateException("Sound has no preview URL"))

            Result.success(previewUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}