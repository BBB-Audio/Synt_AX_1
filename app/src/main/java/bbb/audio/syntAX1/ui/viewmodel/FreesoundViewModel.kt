package bbb.audio.syntAX1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bbb.audio.syntAX1.BuildConfig
import bbb.audio.syntAX1.domain.DownloadSoundUseCase
import bbb.audio.syntAX1.domain.SearchSoundsUseCase
import bbb.audio.syntAX1.domain.ValidatePreviewUrlUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class FreesoundViewModel(
    private val searchSoundsUseCase: SearchSoundsUseCase,
    private val validatePreviewUseCase: ValidatePreviewUrlUseCase,
    private val downloadUseCase: DownloadSoundUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FreesoundUiState())
    val uiState = _uiState.asStateFlow()

    private val _currentlyPlayingId = MutableStateFlow<Int?>(null)
    val currentlyPlayingId = _currentlyPlayingId.asStateFlow()

    private val _previewUrl = MutableStateFlow<String?>(null)
    val previewUrl = _previewUrl.asStateFlow()

    fun onEvent(event: FreesoundEvent) {
        when (event) {
            is FreesoundEvent.Search -> search(event.query)
            is FreesoundEvent.TogglePreview -> togglePreview(event.soundId)
            FreesoundEvent.StopPreview -> stopPreview()
            FreesoundEvent.DismissError -> dismissError()
            else -> { /* Other events are handled directly or in the UI */ }
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = searchSoundsUseCase(query, BuildConfig.FREESOUND_API_KEY)
            result.fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(isLoading = false, sounds = response.results)
                    }
                },
                onFailure = { exception ->
                    val errorState = mapExceptionToErrorState(exception)
                    _uiState.update { it.copy(isLoading = false, error = errorState) }
                }
            )
        }
    }

    fun saveSoundToLibrary(soundId: Int, customName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                downloadUseCase(soundId, BuildConfig.FREESOUND_API_KEY, customName)
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "Sample '$customName' saved!")
                }
            } catch (e: Exception) {
                val errorState = mapExceptionToErrorState(e)
                _uiState.update { it.copy(isLoading = false, error = errorState) }
            }
        }
    }

    private fun togglePreview(soundId: Int) {
        viewModelScope.launch {
            if (_currentlyPlayingId.value == soundId) {
                stopPreview()
            } else {
                stopPreview()
                _currentlyPlayingId.value = soundId
                _uiState.update { it.copy(error = null) }

                val result = validatePreviewUseCase(soundId, BuildConfig.FREESOUND_API_KEY)
                result.fold(
                    onSuccess = { url -> _previewUrl.value = url },
                    onFailure = { exception ->
                        val errorState = mapExceptionToErrorState(exception)
                        _uiState.update { state -> state.copy(error = errorState) }
                        _currentlyPlayingId.value = null
                    }
                )
            }
        }
    }

    private fun stopPreview() {
        _currentlyPlayingId.value = null
        _previewUrl.value = null
    }

    private fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    private fun mapExceptionToErrorState(exception: Throwable): ErrorState {
        return when (exception) {
            is HttpException -> {
                val message = when (exception.code()) {
                    401 -> "Unauthorized: Invalid API Key."
                    404 -> "The requested resource could not be found."
                    429 -> "Too many requests. Please wait a moment."
                    else -> "Server error (${exception.code()}). Please try again later."
                }
                ErrorState(code = exception.code(), message = message)
            }
            is IOException -> {
                ErrorState(code = 0, message = "Network error. Please check your internet connection.")
            }
            is IllegalArgumentException -> {
                ErrorState(code = -2, message = exception.message ?: "Invalid input.")
            }
            else -> {
                ErrorState(code = -1, message = exception.message ?: "An unknown error occurred.")
            }
        }
    }
}