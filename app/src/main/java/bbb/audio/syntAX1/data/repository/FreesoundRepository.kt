package bbb.audio.syntAX1.data.repository

import bbb.audio.syntAX1.data.remote.FreesoundApiService

class FreesoundRepository(
    private val apiService: FreesoundApiService,
    private val sampleRepository: SampleRepositoryInterface
) {

    suspend fun searchSounds(query: String, apiKey: String) =
        apiService.searchSounds(query, apiKey)

    suspend fun getSoundDetail(soundId: Int, apiKey: String) =
        apiService.getSoundDetail(soundId, apiKey)


}
