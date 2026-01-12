package bbb.audio.syntAX1.domain

import bbb.audio.syntAX1.data.local.entity.Sample
import bbb.audio.syntAX1.data.repository.SampleRepositoryInterface
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving all samples from the local database.
 * This provides a clean API for the ViewModel to access sample data.
 */
class GetSamplesUseCase(
    private val sampleRepository: SampleRepositoryInterface
) {
    /**
     * Returns a [Flow] of all samples in the database.
     * The flow automatically updates when samples are added or removed.
     */
    operator fun invoke(): Flow<List<Sample>> {
        return sampleRepository.getAllSamples()
    }
}