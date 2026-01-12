package bbb.audio.syntAX1.ui.viewmodel

import androidx.lifecycle.ViewModel
import bbb.audio.syntAX1.data.repository.SampleScrubberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SampleScrubberViewModel(
    private val repository: SampleScrubberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SampleScrubberState())
    val uiState: StateFlow<SampleScrubberState> = _uiState.asStateFlow()

    fun onChunkSizeChange(size: Float) {
        _uiState.update { it.copy(chunkSize = size) }
        repository.setChunkSize(size)
    }

    fun onReadPointChange(point: Float) {
        _uiState.update { it.copy(readPoint = point) }
        repository.setReadPoint(point)
    }

    fun onTranspositionChange(cents: Float) {
        _uiState.update { it.copy(transposition = cents) }
        repository.setTransposition(cents)
    }

    fun onSpeedRatioChange(ratio: Float) {
        _uiState.update { it.copy(speedRatio = ratio) }
        repository.setSpeedRatio(ratio)
    }

    fun onVolumeChange(vol: Float) {
        _uiState.update { it.copy(volume = vol) }
        repository.setVolume(vol)
    }

    //    fun toggleRecord() {
//        val isRecording = !_uiState.value.isRecording
//        _uiState.update { it.copy(isRecording = isRecording) }
//        if (isRecording) {
//            repository.startRecord()
//        } else {
//            repository.stopRecord()
//        }
//    }
    fun triggerRecord() {
        repository.record()
    }

    fun reset() {
        _uiState.update { it.copy(readPoint = 0f) }
        repository.reset()
    }

    fun toggleMute() {
        val isMuted = !_uiState.value.isMuted
        _uiState.update { it.copy(isMuted = isMuted) }
        repository.toggleMute(isMuted)
    }
}