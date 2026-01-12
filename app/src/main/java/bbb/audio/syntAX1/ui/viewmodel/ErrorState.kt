package bbb.audio.syntAX1.ui.viewmodel

/**
 * A structured data class to hold specific error information for the UI.
 * This allows the UI to react to different kinds of errors.
 */
data class ErrorState(
    val code: Int,
    val message: String
)
