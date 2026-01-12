package bbb.audio.syntAX1.ui.viewmodel

import bbb.audio.syntAX1.data.model.FreesoundSound

/**
 * Represents the complete state for the Freesound search UI.
 */
data class FreesoundUiState(
    val sounds: List<FreesoundSound> = emptyList(),
    val isLoading: Boolean = false,
    val error: ErrorState? = null, // Changed from String? to our new structured ErrorState
    val successMessage: String? = null
)
