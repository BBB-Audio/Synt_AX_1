package bbb.audio.syntAX1.domain

import bbb.audio.syntAX1.data.model.SearchResponse
import bbb.audio.syntAX1.data.repository.FreesoundRepository

/**
 * Use case for searching sounds on Freesound.
 * Contains the business logic for sound searching.
 */
class SearchSoundsUseCase(
    private val freesoundRepository: FreesoundRepository
) {
    /**
     * Executes a sound search with the given query.
     * 
     * @param query The search query (must not be empty)
     * @param apiKey The Freesound API key for authentication
     * @return A [Result] containing the search results on success,
     *         or an exception on failure
     */
    suspend operator fun invoke(query: String, apiKey: String): Result<SearchResponse> {
        // BUSINESS RULE: Query validation
        if (query.isBlank()) {
            return Result.failure(IllegalArgumentException("Search query cannot be empty"))
        }
        
        // BUSINESS RULE: Query length limit
        if (query.length > 100) {
            return Result.failure(IllegalArgumentException("Search query too long"))
        }
        
        // BUSINESS RULE: Actual search execution
        return try {
            val response = freesoundRepository.searchSounds(query, apiKey)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}