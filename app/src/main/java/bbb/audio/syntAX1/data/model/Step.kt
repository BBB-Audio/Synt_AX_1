package bbb.audio.syntAX1.data.model

/**
 * Domain Model - Represents a single sequencer step
 * Used between ViewModel → Repository → Engine
 */
data class Step(
    val id: Int,
    val isOn: Boolean = false,
    val notePitch: Float = 60f,          // MIDI note number
    val veloKnobValue: Float = 0.8f,     // Velocity (0-1)
    val freeKnobValue: Float = 0.5f,     // Free parameter
    val stepLength: Long = 500L           // Step duration in ms
)