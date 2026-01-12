package bbb.audio.syntAX1.ui.viewmodel

data class SynthState(
    val filter: FilterState = FilterState(),
    val envelope: EnvelopeState = EnvelopeState(),
    val chorus: ChorusState = ChorusState(),
    val reverb: ReverbState = ReverbState(),
    val delay: DelayState = DelayState(),
    val mainVolume: Float = 0.7f,
    val subVolume: Float = 0.0f,
    val midiActivity: Boolean = false,
    val beatIndicator: Boolean = false
)

data class FilterState(
    val cutoff: Float = 0.5f,
    val resonance: Float = 0.5f,
    val type: FilterType = FilterType.LOW_PASS
)

data class EnvelopeState(
    val attack: Float = 0.1f,
    val decay: Float = 0.2f,
    val sustain: Float = 0.7f,
    val release: Float = 0.3f
)

data class ChorusState(
    val depth: Float = 0.0f,
    val lfoSpeed: Float = 0.5f
)
data class ReverbState(
    val depth: Float = 0.0f,
    val feed: Float = 0.09f,
    val crossfreq: Float = 0.3f,
    val damp: Float = 0.2f
)
data class DelayState(
    val amount: Float = 0.0f,
    val time: Float = 0.5f,
    val feed: Float = 0.3f
)

enum class FilterType {
    LOW_PASS, HIGH_PASS
}
