package bbb.audio.syntAX1.ui.viewmodel

/**
 * App-wide settings
 */
data class AppSettingsUiState(
    val sampleRate: Int = 44100,
    val outputVolume: Float = 0.8f,
    val quantizeMidi: Boolean = true,
    val usbMidiEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)