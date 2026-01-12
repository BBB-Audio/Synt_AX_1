package bbb.audio.syntAX1.domain

import bbb.audio.syntAX1.data.repository.SampleRepositoryInterface


/**
 * Use case for deleting a sample from the local database.
 * Encapsulates the deletion logic for better testability.
 */
class DeleteSampleUseCase(
    private val sampleRepository: SampleRepositoryInterface
) {
    /**
     * Deletes a sample by its ID.
     *
     * @param sampleId The unique identifier of the sample to delete
     * @return `true` if deletion was successful, `false` otherwise
     */
    suspend operator fun invoke(sampleId: Long): Boolean {
        return sampleRepository.deleteSample(sampleId)
    }
}