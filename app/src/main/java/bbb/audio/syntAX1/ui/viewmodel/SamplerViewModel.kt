package bbb.audio.syntAX1.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bbb.audio.syntAX1.data.engine.puredata.PdEngineManager
import bbb.audio.syntAX1.data.local.entity.Sample
import bbb.audio.syntAX1.data.repository.SampleRepository
import bbb.audio.syntAX1.data.repository.SamplerRepository
import bbb.audio.syntAX1.domain.LoadSampleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Sampler screen.
 *
 * Now uses the merged SamplerRepository which handles:
 * - Audio loading (bytes → Pd array)
 * - Playback control (speed, volume)
 * - Parameter control (filter, envelope)
 */
class SamplerViewModel(
    private val samplerRepository: SamplerRepository,
    private val sampleRepository: SampleRepository,
    private val loadSampleUseCase: LoadSampleUseCase,
    private val pdEngineManager: PdEngineManager? = null
) : ViewModel() {

    private val _samples = MutableStateFlow<List<Sample>>(emptyList())
    val samples = _samples.asStateFlow()

    private val _uiState = MutableStateFlow(SamplerState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sampleRepository.getAllSamples()
                .collect { _samples.value = it }
        }
    }

    // ==================== SAMPLE LOADING ====================

    /**
     * Load a sample from the local database and into the Pd engine.
     */
    fun loadSampleFromFile(sample: Sample) {
        viewModelScope.launch {
            try {
                Log.d("SamplerViewModel", "Loading sample: ${sample.name}")

                // Load into Pd array
                val success = samplerRepository.loadLoopSample(
                    sample.audioData,
                    sample.name
                )

                if (success) {
                    Log.i("SamplerViewModel", "✓ Sample loaded: ${sample.name}")

                    // Update state to reflect loaded sample
                    _uiState.update {
                        it.copy(sOsc = it.sOsc.copy(loadedSample = sample))
                    }

                    // Sync speed to current BPM if engine available
                    pdEngineManager?.let {
                        val speedMultiplier = samplerRepository.calculateSpeedForBpm(it.getCurrentBpm())
                        samplerRepository.setLoopSpeed(speedMultiplier)
                    }
                } else {
                    Log.e("SamplerViewModel", "Failed to load sample")
                }
            } catch (e: Exception) {
                Log.e("SamplerViewModel", "Error loading sample: ${e.message}", e)
            }
        }
    }

    /**
     * Load sample from raw bytes (alternative path).
     */
    fun loadSampleBytes(bytes: ByteArray, context: Context) {
        viewModelScope.launch {
            try {
                samplerRepository.loadLoopSample(bytes, "custom_sample")
                Log.d("SamplerViewModel", "Loaded sample from bytes (${bytes.size})")
            } catch (e: Exception) {
                Log.e("SamplerViewModel", "Error loading bytes: ${e.message}")
            }
        }
    }

    // ==================== OSC STATE (Pitch, Speed) ====================

    fun onPitchChange(newPitch: Float) {
        _uiState.update { it.copy(sOsc = it.sOsc.copy(sPitch = newPitch)) }
        samplerRepository.setPitch(newPitch)
    }

    fun onPlaybackSpeedChange(newSpeed: Float) {
        _uiState.update { it.copy(sOsc = it.sOsc.copy(playbackSpeed = newSpeed)) }
        samplerRepository.setPlaybackSpeed(newSpeed)
    }

    // ==================== FILTER STATE ====================

    fun onCutoffChange(newCutoff: Float) {
        _uiState.update { it.copy(sFilter = it.sFilter.copy(sCutoff = newCutoff)) }
        samplerRepository.setCutoff(newCutoff)
    }

    fun onResonanceChange(newResonance: Float) {
        _uiState.update { it.copy(sFilter = it.sFilter.copy(sResonance = newResonance)) }
        samplerRepository.setResonance(newResonance)
    }

    fun onFilterTypeChange(newType: FilterType) {
        _uiState.update { it.copy(sFilter = it.sFilter.copy(sType = newType)) }
        samplerRepository.setFilterType(newType)
    }

    // ==================== ENVELOPE STATE ====================

    fun onAttackChange(newAttack: Float) {
        _uiState.update { it.copy(sEnvelope = it.sEnvelope.copy(sAttack = newAttack)) }
        samplerRepository.setAttack(newAttack)
    }

    fun onDecayChange(newDecay: Float) {
        _uiState.update { it.copy(sEnvelope = it.sEnvelope.copy(sDecay = newDecay)) }
        samplerRepository.setDecay(newDecay)
    }

    fun onSustainChange(newSustain: Float) {
        _uiState.update { it.copy(sEnvelope = it.sEnvelope.copy(sSustain = newSustain)) }
        samplerRepository.setSustain(newSustain)
    }

    fun onReleaseChange(newRelease: Float) {
        _uiState.update { it.copy(sEnvelope = it.sEnvelope.copy(sRelease = newRelease)) }
        samplerRepository.setRelease(newRelease)
    }

    // ==================== PLAYBACK CONTROL ====================

    /**
     * Set loop volume.
     */
    fun setLoopVolume(volume: Float) {
        samplerRepository.setLoopVolume(volume)
        Log.d("SamplerViewModel", "Loop volume set to: $volume")
    }

    /**
     * Trigger sample (for future single-shot samples).
     */
    fun triggerSample() {
        samplerRepository.triggerSample(60)
        Log.d("SamplerViewModel", "Sample triggered")
    }

    // ==================== BPM SYNCHRONIZATION ====================

    /**
     * Called when BPM changes to re-synchronize loop playback speed.
     * Should be called from PdEngineManager when setClockBpm() is invoked.
     */
    fun syncLoopToBpm(bpm: Float, originalBpm: Float = 120f) {
        val speedMultiplier = samplerRepository.calculateSpeedForBpm(bpm, originalBpm)
        samplerRepository.setLoopSpeed(speedMultiplier)
        Log.d("SamplerViewModel", "Loop synced to BPM: $bpm")
    }
}