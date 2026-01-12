package bbb.audio.syntAX1.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bbb.audio.syntAX1.data.local.entity.Sample
import bbb.audio.syntAX1.domain.DeleteSampleUseCase
import bbb.audio.syntAX1.domain.GetSamplesUseCase
import bbb.audio.syntAX1.domain.PlaySamplePreviewUseCase
import bbb.audio.syntAX1.domain.ShareSampleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val getSamplesUseCase: GetSamplesUseCase,
    private val deleteSampleUseCase: DeleteSampleUseCase,
    private val playSamplePreviewUseCase: PlaySamplePreviewUseCase,
    private val shareSampleUseCase: ShareSampleUseCase
) : ViewModel() {


    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Flow of all samples from the database.
     * Automatically updates when samples are added or removed.
     */
    val samples: StateFlow<List<Sample>> = getSamplesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /**
     * Triggers the use case to share a sample.
     * The context is required by the Android share mechanism and is passed from the UI.
     */
    fun shareSample(context: Context, sample: Sample) {
        val result = shareSampleUseCase(context, sample)
        result.onFailure { exception ->
            _uiState.update { it.copy(error = exception.message ?: "Failed to share sample") }
        }
        // On success, the OS handles the UI, so I don't need to show a message here.
    }

    /**
     * Deletes a sample from the database.
     *
     * @param sampleId The ID of the sample to delete
     */
    fun deleteSample(sampleId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }

            val result = deleteSampleUseCase(sampleId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    successMessage = if (result) "Sample deleted" else "Failed to delete sample"
                )
            }
        }
    }

    /**
     * Plays a preview of the given sample.
     */
    fun playSamplePreview(sample: Sample) {
        viewModelScope.launch {
            playSamplePreviewUseCase(sample)
        }
    }

    override fun onCleared() {
        playSamplePreviewUseCase.release()
        super.onCleared()
    }

    /**
     * Clears any displayed messages (error or success).
     */
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}



//    // --- Patches ---
//    fun savePatch(name: String, synthState: SynthState) {
//        viewModelScope.launch {
//            val patch = PatchEntity(name = name, synthState = synthState)
//            patchRepository.insertPatch(patch)
//        }
//    }
//
//    fun loadPatch(id: Long) {
//        viewModelScope.launch {
//            val patch = patchRepository.getPatchById(id)
//            // TODO: Update the UI with the loaded patch
//        }
//    }
//
//    // --- Samples ---
//    fun saveSample(name: String, audioFile: File) {
//        viewModelScope.launch {
//            val sample = SampleEntity(name = name, filePath = audioFile.absolutePath)
//            sampleRepository.insertSample(sample)
//        }
//    }
//
//    fun loadSample(id: Long) {
//        viewModelScope.launch {
//            val sample = sampleRepository.getSampleById(id)
//            // TODO: Use the loaded sample
//        }
//    }
