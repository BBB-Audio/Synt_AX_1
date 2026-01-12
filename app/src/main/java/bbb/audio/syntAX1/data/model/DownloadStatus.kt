package bbb.audio.syntAX1.data.model

sealed class DownloadStatus {
    object Idle : DownloadStatus()
    object Loading : DownloadStatus()
    data class Success(val message: String) : DownloadStatus()
    data class Error(val message: String) : DownloadStatus()
}