package bbb.audio.syntAX1.ui.viewmodel

import bbb.audio.syntAX1.data.local.entity.Pattern

/**
 * Represents the state of a single step in the sequencer.
 */
data class SequencerStepState(
    val id: Int,
    val isOn: Boolean = false,
    val freeKnobValue: Float = 0.5f,    // FreeLane parameter
    val veloKnobValue: Float = 0.8f,
    val notePitch: Float = 60f,         // Middle C
    val stepLength: Long = 500L
)

/**
 * Represents the entire state of the Sequencer UI.
 */
data class SequencerUiState(
    val steps: List<SequencerStepState> = List(16) { index -> SequencerStepState(id = index) },
    val playingStepIndex: Int? = null,
    val isFreeLaneVisible: Boolean = false,
    val bpm: Float = 120f,
    val isPlaying: Boolean = false,
    val savedPatterns: List<Pattern> = emptyList(),
    val isBeatActive: Boolean = false
)
