package bbb.audio.syntAX1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bbb.audio.syntAX1.data.model.Step
import bbb.audio.syntAX1.data.repository.PatternRepository
import bbb.audio.syntAX1.data.repository.SequencerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * SequencerViewModel - Pure UI Logic & State Management
 *
 * Responsibilities:
 * - Manage UI state (steps, BPM, playback status)
 * - Listen to clock ticks from Repository
 * - Delegate business logic to Repository
 * - Handle UI events from Composables
 *
 * Dependencies:
 * - SequencerRepository (business logic facade)
 * - PatternRepository (pattern save/load)
 */
class SequencerViewModel(
    private val sequencerRepository: SequencerRepository,
    private val patternRepository: PatternRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SequencerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        Log.d(TAG, "SequencerViewModel init: Setting up clock tick subscription...")

        // Listen to clock ticks from Repository
        // When a tick arrives, update UI and delegate to Repository for business logic
        viewModelScope.launch {
            sequencerRepository.getClockTickFlow().onEach { stepIndex ->
                handleClockTick(stepIndex)
            }.launchIn(viewModelScope)
        }

        // Load saved patterns
        patternRepository.getAllPatterns()
            .onEach { patterns ->
                _uiState.update { it.copy(savedPatterns = patterns) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Called every time the clock ticks (each step)
     * 1. Find the step in UI state
     * 2. Delegate to Repository for business logic (play note if ON)
     * 3. Update UI state (LED indicator)
     */
    private fun handleClockTick(stepIndex: Int) {
        val uiStep = _uiState.value.steps.find { it.id == stepIndex }

        if (uiStep != null) {
            // Convert UI Model → Domain Model
            val domainStep = Step(
                id = uiStep.id,
                isOn = uiStep.isOn,
                notePitch = uiStep.notePitch,
                veloKnobValue = uiStep.veloKnobValue,
                freeKnobValue = uiStep.freeKnobValue,
                stepLength = uiStep.stepLength
            )
            sequencerRepository.handleSequencerTick(domainStep)
        }

        _uiState.update { it.copy(playingStepIndex = stepIndex) }
    }

    // ============ Step Control ============

    fun onStepToggled(stepId: Int) {
        _uiState.update { currentState ->
            val newSteps = currentState.steps.map { step ->
                if (step.id == stepId) {
                    step.copy(isOn = !step.isOn)
                } else {
                    step
                }
            }
            currentState.copy(steps = newSteps)
        }
        Log.d(TAG, "Step $stepId toggled")
    }

    // ============ Step Note Control ============
    fun onNotePitchChanged(stepId: Int, notePitch: Int) {
        _uiState.update { currentState ->
            val newSteps = currentState.steps.map {
                if (it.id == stepId) it.copy(notePitch = notePitch.toFloat()) else it
            }
            currentState.copy(steps = newSteps)
        }
    }

    fun onStepLengthChanged(stepId: Int, length: Long) {
        _uiState.update { currentState ->
            val newSteps = currentState.steps.map {
                if (it.id == stepId) it.copy(stepLength = length) else it
            }
            currentState.copy(steps = newSteps)
        }
    }

    // ============ FreeLane Control ============
    fun onFreeKnobChanged(stepId: Int, newValue: Float) {
        _uiState.update { currentState ->
            val newSteps = currentState.steps.map { step ->
                if (step.id == stepId) {
                    step.copy(freeKnobValue = newValue)
                } else {
                    step
                }
            }
            currentState.copy(steps = newSteps)
        }
    }

    // ============ Velocity Control ============
    fun onVeloKnobChanged(stepId: Int, newValue: Float) {
        _uiState.update { currentState ->
            val newSteps = currentState.steps.map {
                if (it.id == stepId) {
                    it.copy(veloKnobValue = newValue)
                } else {
                    it
                }
            }
            currentState.copy(steps = newSteps)
        }
        Log.d(TAG, "Velocity for step $stepId changed to $newValue")
    }

    // ============ Playback Control ============
    fun startStopPlayback() {
        val isCurrentlyPlaying = sequencerRepository.isClockRunning()

        if (isCurrentlyPlaying) {
            // STOP
            sequencerRepository.stopClock()
            _uiState.update {
                it.copy(
                    playingStepIndex = null,
                    isPlaying = false
                )
            }
            Log.i(TAG, "Playback stopped")
        } else {
            // START
            sequencerRepository.startClock()
            _uiState.update { it.copy(isPlaying = true) }
            Log.i(TAG, "Playback started")
        }
    }

    fun panic() {
        sequencerRepository.panic()
    }

    // ============ BPM Control ============
    fun onBpmChanged(newBpm: Float) {
        sequencerRepository.setBpm(newBpm)
        _uiState.update { it.copy(bpm = newBpm) }
        Log.d(TAG, "BPM changed to $newBpm")
    }

    // ============ FreeLane Visibility ============
    fun onToggleFreeLaneVisibility() {
        _uiState.update { it.copy(isFreeLaneVisible = !it.isFreeLaneVisible) }
        Log.d(TAG, "FreeLane visibility toggled")
    }

    // ============ Pattern Management ============
    fun savePattern(name: String, description: String = "") {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                patternRepository.savePattern(
                    name = name,
                    steps = currentState.steps,
                    bpm = currentState.bpm,
                    description = description
                )
                Log.i(TAG, "✓ Pattern saved: $name")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving pattern: ${e.message}")
            }
        }
    }

    fun loadPattern(patternId: Long) {
        viewModelScope.launch {
            try {
                val pattern = patternRepository.loadPattern(patternId)
                if (pattern != null) {
                    val steps = patternRepository.parsePatternSteps(pattern)
                    _uiState.update {
                        it.copy(
                            steps = steps,
                            bpm = pattern.bpm
                        )
                    }
                    Log.i(TAG, "✓ Pattern loaded: ${pattern.name}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading pattern: ${e.message}")
            }
        }
    }

    // ============ Helper ============

    fun isStepActive(stepId: Int): Boolean {
        return _uiState.value.playingStepIndex == stepId
    }

    companion object {
        private const val TAG = "SequencerViewModel"
    }
}