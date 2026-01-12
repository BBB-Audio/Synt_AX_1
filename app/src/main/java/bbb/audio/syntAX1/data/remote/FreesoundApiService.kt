package bbb.audio.syntAX1.data.remote

import bbb.audio.syntAX1.data.model.FreesoundSoundDetail
import bbb.audio.syntAX1.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FreesoundApiService {
    @GET("search/text/")
    suspend fun searchSounds(
        @Query("query") query: String,
        @Query("token") apiKey: String,
        @Query("page_size") pageSize: Int = 20
    ): SearchResponse

    @GET("sounds/{id}/")
    suspend fun getSoundDetail(
        @Path("id") soundId: Int,
        @Query("token") apiKey: String
    ): FreesoundSoundDetail
}

