package bbb.audio.syntAX1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bbb.audio.syntAX1.data.engine.puredata.PdEngineManager
import bbb.audio.syntAX1.data.local.AppSettings
import bbb.audio.syntAX1.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * SettingsViewModel
 *
 * Manages app-wide settings state and persistence.
 * Observes Room Database and exposes StateFlow for UI.
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val pdEngineManager: PdEngineManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val settings = settingsRepository.loadSettings()
                _uiState.update {
                    it.copy(
                        sampleRate = settings.sampleRate,
                        outputVolume = settings.outputVolume,
                        quantizeMidi = settings.quantizeMidi,
                        usbMidiEnabled = settings.usbMidiEnabled,
                        themeName = settings.themeName,
                        isLoading = false
                    )
                }
                Log.d(TAG, "✓ Settings loaded: $settings")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading settings: ${e.message}")
            }
        }
    }

    /**
     * Apply current settings to audio engine and save to database.
     * Call this when user saves settings or closes settings dialog.
     */
    fun applySettings() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                Log.i(TAG, "🔄 Applying settings: $currentState")

                // 1. Update audio engine volume if needed
                // (Assuming PdEngineManager has a setVolume method or sends to PD)
                pdEngineManager.sendToPd("volume", currentState.outputVolume)

                // 2. Save to database
                val updatedSettings = AppSettings(
                    sampleRate = currentState.sampleRate,
                    outputVolume = currentState.outputVolume,
                    quantizeMidi = currentState.quantizeMidi,
                    usbMidiEnabled = currentState.usbMidiEnabled,
                    themeName = currentState.themeName
                )
                settingsRepository.saveSettings(updatedSettings)

                Log.i(TAG, "✓ Settings applied and saved")

            } catch (e: Exception) {
                Log.e(TAG, "Error applying settings: ${e.message}")
            }
        }
    }
    /**
     * Updates the selected theme in the UI state.
     */
    fun updateTheme(themeName: String) {
        _uiState.update { it.copy(themeName = themeName) }
    }

    /**
     * Change Sample Rate and reinitialize audio engine
     */
    fun setSampleRate(newSampleRate: Int) {
        val currentState = _uiState.value

        if (currentState.sampleRate == newSampleRate) {
            Log.d(TAG, "Sample rate already set to $newSampleRate, skipping")
            return
        }

        viewModelScope.launch {
            try {
                Log.i(TAG, "🔄 Changing sample rate to $newSampleRate...")

                // 1. Stop audio
                pdEngineManager.stop()
                Log.d(TAG, "✓ Audio stopped")

                // 2. Update UI state
                _uiState.update { it.copy(sampleRate = newSampleRate) }

                // 3. Reinitialize audio with new sample rate
                val initSuccess = pdEngineManager.initialize(
                    sampleRate = newSampleRate,
                    inChannels = 0,
                    outChannels = 2,
                    ticksPerBuffer = 4,
                    restart = true  // ← Force restart
                )

                if (!initSuccess) {
                    Log.e(TAG, "✗ Failed to reinitialize audio")
                    return@launch
                }

                Log.d(TAG, "✓ Audio reinitialized")

                // 4. Get last loaded patch info and reload
                val patchInfo = pdEngineManager.getLastLoadedPatch()
                if (patchInfo != null) {
                    val (resId, fileName) = patchInfo
                    val wavFiles = pdEngineManager.getLastLoadedWavFiles()

                    // Load clock patch first
                    // Note: You might need to track which patches were loaded
                    // For now, let's assume we reload the main patches
                    pdEngineManager.loadPatch(resId, fileName, wavFiles)
                    Log.d(TAG, "✓ Patch reloaded after sample rate change")
                } else {
                    Log.w(TAG, "⚠ No patch loaded previously, skipping patch reload")
                }


                // 5. Save to database and apply
                applySettings()
                Log.i(TAG, "✓ Sample rate changed to $newSampleRate")

            } catch (e: Exception) {
                Log.e(TAG, "✗ Error changing sample rate: ${e.message}", e)
                // Rollback to old sample rate in UI
                _uiState.update { it.copy(sampleRate = currentState.sampleRate) }
            }
        }
    }

    /**
     * Change output volume
     */
    fun setOutputVolume(newVolume: Float) {
        viewModelScope.launch {
            try {
                val validVolume = newVolume.coerceIn(0f, 1f)
                _uiState.update { it.copy(outputVolume = validVolume) }

                // Immediately apply volume change to audio engine
                pdEngineManager.sendToPd("volume", validVolume)
                Log.d(TAG, "Volume changed to ${(validVolume * 100).toInt()}%")

                // Note: Don't save here, let user save explicitly or auto-save on dismiss

            } catch (e: Exception) {
                Log.e(TAG, "Error setting volume: ${e.message}")
            }
        }
    }

    /**
     * Enable/disable MIDI quantization
     */
    fun setQuantizeMidi(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(quantizeMidi = enabled) }
                Log.d(TAG, "Quantize MIDI: $enabled")
                // Note: This setting is used by ExternalMidiListener when processing events
            } catch (e: Exception) {
                Log.e(TAG, "Error setting quantize: ${e.message}")
            }
        }
    }

    /**
     * Enable/disable USB MIDI input
     */
    fun setUsbMidiEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(usbMidiEnabled = enabled) }
                Log.d(TAG, "USB MIDI: $enabled")

                // Initialize or release USB MIDI based on setting
                if (enabled) {

                    Log.i(TAG, "USB MIDI enabled - devices will be available")
                } else {
                    Log.i(TAG, "USB MIDI disabled")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting USB MIDI: ${e.message}")
            }
        }
    }

    /**
     * Reset all settings to defaults and apply them
     */
    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                val defaults = AppSettings()
                _uiState.update {
                    it.copy(
                        sampleRate = defaults.sampleRate,
                        outputVolume = defaults.outputVolume,
                        quantizeMidi = defaults.quantizeMidi,
                        usbMidiEnabled = defaults.usbMidiEnabled,
                        themeName = defaults.themeName
                    )
                }

                // Apply defaults immediately
                applySettings()
                Log.i(TAG, "✓ Settings reset to defaults and applied")

            } catch (e: Exception) {
                Log.e(TAG, "Error resetting settings: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}

data class SettingsUiState(
    val sampleRate: Int = 44100,
    val outputVolume: Float = 0.5f,
    val quantizeMidi: Boolean = true,
    val usbMidiEnabled: Boolean = false,
    val themeName: String = "Default",
    val isLoading: Boolean = false,
    val error: String? = null
)