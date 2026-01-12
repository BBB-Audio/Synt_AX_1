package bbb.audio.syntAX1.ui.viewmodel

import bbb.audio.syntAX1.data.model.FreesoundSound

/**
 * Defines all possible user interactions (events) that can occur on the Freesound search UI.
 */
sealed interface FreesoundEvent {
    data class Search(val query: String) : FreesoundEvent
    data class SelectSound(val sound: FreesoundSound) : FreesoundEvent
    data class TogglePreview(val soundId: Int) : FreesoundEvent
    object StopPreview : FreesoundEvent
    object Dismiss : FreesoundEvent
    object DismissError : FreesoundEvent // Added for the new error handling
}
