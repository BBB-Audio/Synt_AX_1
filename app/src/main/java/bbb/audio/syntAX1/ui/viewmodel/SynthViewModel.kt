package bbb.audio.syntAX1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bbb.audio.syntAX1.data.repository.SynthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SynthViewModel(private val synthRepository: SynthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SynthState())
    val uiState = _uiState.asStateFlow()

    init {
        synthRepository.midiActivityFlow
            .onEach { isActive ->
                if (isActive) {
                    _uiState.update { it.copy(midiActivity = true) }
                    viewModelScope.launch {
                        delay(100)
                        _uiState.update { it.copy(midiActivity = false) }
                    }
                }
            }
            .launchIn(viewModelScope)

        // Beat indicator for visual metronome
//        synthRepository.beatIndicatorFlow
//            .onEach { isBeat ->
//                Log.d("SynthVM", "Beat indicator: $isBeat")
//                _uiState.update { it.copy(beatIndicator = isBeat) }
//                if (isBeat) {
//                    viewModelScope.launch {
//                        delay(22)
//                        _uiState.update { it.copy(beatIndicator = false) }
//                    }
//                }
//            }
//            .launchIn(viewModelScope)

        // Initialize synth with default values from the UI state
        val initialState = _uiState.value
        synthRepository.setMainVolume(initialState.mainVolume)
        synthRepository.setFilterType(initialState.filter.type)
        synthRepository.setCutoff(initialState.filter.cutoff)
        synthRepository.setResonance(initialState.filter.resonance)
        synthRepository.setAttack(initialState.envelope.attack)
        synthRepository.setDecay(initialState.envelope.decay)
        synthRepository.setSustain(initialState.envelope.sustain)
        synthRepository.setRelease(initialState.envelope.release)
        synthRepository.setChorusDepth(initialState.chorus.depth)
        synthRepository.setLfoSpeed(initialState.chorus.lfoSpeed)
        synthRepository.setReverbDepth(initialState.reverb.depth)
        synthRepository.setReverbFeed(initialState.reverb.feed)
        synthRepository.setReverbCrossfreq(initialState.reverb.crossfreq)
        synthRepository.setReverbDamp(initialState.reverb.damp)
        synthRepository.setDelayAmount(initialState.delay.amount)
        synthRepository.setDelayTime(initialState.delay.time)
        synthRepository.setDelayFeed(initialState.delay.feed)
        synthRepository.setSubVolume(initialState.subVolume)
    }

    fun onCutoffChange(newValue: Float) {
        _uiState.update { currentState ->
            val newFilterState = currentState.filter.copy(cutoff = newValue)
            currentState.copy(filter = newFilterState)
        }
        synthRepository.setCutoff(newValue)
    }

    fun onResonanceChange(newValue: Float) {
        _uiState.update { currentState ->
            val newFilterState = currentState.filter.copy(resonance = newValue)
            currentState.copy(filter = newFilterState)
        }
        synthRepository.setResonance(newValue)
    }

    fun onFilterTypeChange(newType: FilterType) {
        _uiState.update { currentState ->
            val newFilterState = currentState.filter.copy(type = newType)
            currentState.copy(filter = newFilterState)
        }
        synthRepository.setFilterType(newType)
    }

    fun onMainVolumeChange(newVolume: Float) {
        _uiState.update { it.copy(mainVolume = newVolume) }
        synthRepository.setMainVolume(newVolume)
    }

    fun playNote(noteNumber: Int, velocity: Int) {
        synthRepository.playNote(noteNumber, velocity)
    }

    fun stopNote(noteNumber: Int) {
        synthRepository.stopNote(noteNumber)
    }

    fun onAttackChange(newAttack: Float) {
        _uiState.update { currentState ->
            val newEnvelopeState = currentState.envelope.copy(attack = newAttack)
            currentState.copy(envelope = newEnvelopeState)
        }
        synthRepository.setAttack(newAttack)
    }

    fun onDecayChange(newDecay: Float) {
        _uiState.update { currentState ->
            val newEnvelopeState = currentState.envelope.copy(decay = newDecay)
            currentState.copy(envelope = newEnvelopeState)
        }
        synthRepository.setDecay(newDecay)
    }

    fun onSustainChange(newSustain: Float) {
        _uiState.update { currentState ->
            val newEnvelopeState = currentState.envelope.copy(sustain = newSustain)
            currentState.copy(envelope = newEnvelopeState)
        }
        synthRepository.setSustain(newSustain)
    }

    fun onReleaseChange(newRelease: Float) {
        _uiState.update { currentState ->
            val newEnvelopeState = currentState.envelope.copy(release = newRelease)
            currentState.copy(envelope = newEnvelopeState)
        }
        synthRepository.setRelease(newRelease)
    }

    fun onChorusDepthChange(newDepth: Float) {
        _uiState.update {
            val newChorusState = it.chorus.copy(depth = newDepth)
            it.copy(chorus = newChorusState)
        }
        synthRepository.setChorusDepth(newDepth)
    }

    fun onLfoSpeedChange(newSpeed: Float) {
        _uiState.update {
            val newChorusState = it.chorus.copy(lfoSpeed = newSpeed)
            it.copy(chorus = newChorusState)
        }
        synthRepository.setLfoSpeed(newSpeed)
    }
    fun onReverbDepthChange(newDepth: Float) {
        _uiState.update {
            val newReverbState = it.reverb.copy(depth = newDepth)
            it.copy(reverb = newReverbState)
        }
        synthRepository.setReverbDepth(newDepth)
    }

    fun onReverbFeedChange(newFeed: Float) {
        _uiState.update {
            val newReverbState = it.reverb.copy(feed = newFeed)
            it.copy(reverb = newReverbState)
        }
        synthRepository.setReverbFeed(newFeed)
    }

    fun onReverbCrossfreqChange(newFreq: Float) {
        _uiState.update {
            val newReverbState = it.reverb.copy(crossfreq = newFreq)
            it.copy(reverb = newReverbState)
        }
        synthRepository.setReverbCrossfreq(newFreq)
    }

    fun onReverbDampChange(newDamp: Float) {
        _uiState.update {
            val newReverbState = it.reverb.copy(damp = newDamp)
            it.copy(reverb = newReverbState)
        }
        synthRepository.setReverbDamp(newDamp)
    }
    fun onDelayAmountChange(newAmount: Float) {
        _uiState.update {
            val newDelayState = it.delay.copy(amount = newAmount)
            it.copy(delay = newDelayState)
        }
        synthRepository.setDelayAmount(newAmount)
    }

    fun onDelayTimeChange(newTime: Float) {
        _uiState.update {
            val newDelayState = it.delay.copy(time = newTime)
            it.copy(delay = newDelayState)
        }
        synthRepository.setDelayTime(newTime)
    }

    fun onDelayFeedChange(newFeed: Float) {
        _uiState.update {
            val newDelayState = it.delay.copy(feed = newFeed)
            it.copy(delay = newDelayState)
        }
        synthRepository.setDelayFeed(newFeed)
    }
    fun onSubVolumeChange(newSubVolume: Float) {
        _uiState.update { it.copy(subVolume = newSubVolume) }
        synthRepository.setSubVolume(newSubVolume)
    }
}