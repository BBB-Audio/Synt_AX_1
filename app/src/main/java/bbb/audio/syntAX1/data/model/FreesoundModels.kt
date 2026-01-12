package bbb.audio.syntAX1.data.model

import com.google.gson.annotations.SerializedName

// Search Response
data class SearchResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<FreesoundSound>
)

data class FreesoundSound(
    val id: Int,
    val name: String,
    val username: String
)

// Sound Detail Response
data class FreesoundSoundDetail(
    val id: Int,
    val name: String,
    val duration: Float,
    val previews: PreviewUrls?,
    val username: String
)

// Shared Preview URLs
data class PreviewUrls(
    @SerializedName("preview-hq-mp3")
    val previewHqMp3: String?
)