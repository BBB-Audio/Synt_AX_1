package bbb.audio.syntAX1.ui.viewmodel

import bbb.audio.syntAX1.data.local.entity.Sample

/**
 * Represents the state of the sample oscillator itself.
 */
data class SampleOscState(
    /** The currently loaded sample from the local database. Null if no sample is loaded. */
    val loadedSample: Sample? = null,
    /** Pitch transposition in semitones, e.g., -12f to +12f. */
    val sPitch: Float = 0.0f,
    /** Playback speed multiplier, e.g., 0.5f for half-speed, 2.0f for double-speed. */
    val playbackSpeed: Float = 1.0f
)

/**
 * Represents the entire state of a single sampler instrument.
 * This will be the main state managed by the SamplerViewModel.
 */
data class SamplerState(
    val sOsc: SampleOscState = SampleOscState(),
    val sEnvelope: SamplerEnvelopeState = SamplerEnvelopeState(),
    val sFilter: SamplerFilterState = SamplerFilterState()
)

/**
 * State for the ADSR envelope.
 */
data class SamplerEnvelopeState(
    val sAttack: Float = 0.01f,      // 0.0f to 1.0f
    val sDecay: Float = 0.2f,        // 0.0f to 1.0f
    val sSustain: Float = 0.7f,      // 0.0f to 1.0f
    val sRelease: Float = 0.3f       // 0.0f to 1.0f
)

/**
 * State for the filter section.
 */
data class SamplerFilterState(
    val sCutoff: Float = 1.0f,       // 0.0f to 1.0f (fully open by default)
    val sResonance: Float = 0.0f,    // 0.0f to 1.0f (no resonance by default)
    val sType: FilterType = FilterType.LOW_PASS
)


