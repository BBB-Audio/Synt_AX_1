package bbb.audio.syntAX1.ui.viewmodel

/**
 * UI state for the Library screen.
 */
data class LibraryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)