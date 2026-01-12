package bbb.audio.syntAX1.ui.viewmodel

data class SampleScrubberState(
    val chunkSize: Float = 100f,           // 0-1000 ms
    val readPoint: Float = 0f,             // -250 to 1000 ms
    val transposition: Float = 0f,         // cents
    val speedRatio: Float = 0f,            // -60 to 60
    val volume: Float = 0.5f,              // 0.0 - 1.0
//    val isRecording: Boolean = false,
    val isMuted: Boolean = false
)