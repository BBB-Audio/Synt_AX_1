package bbb.audio.syntAX1.domain

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import bbb.audio.syntAX1.data.local.entity.Sample
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Use case for playing sample previews.
 * Encapsulates audio playback logic with proper resource management.
 */
class PlaySamplePreviewUseCase(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "PlaySamplePreviewUseCase"
        private const val PREVIEW_TIMEOUT_MS = 5000L
    }
    
    private var currentPlayer: MediaPlayer? = null
    private var currentJob: Job? = null
    
    /**
     * Plays a preview of the given sample.
     * Automatically stops after 5 seconds or when sample ends.
     */
    operator fun invoke(sample: Sample) {
        cancelCurrentPlayback()
        
        Log.d(TAG, "Playing preview: ${sample.name}")
        Log.d(TAG, "File URI: '${sample.fileUri}'")
        Log.d(TAG, "Audio data size: ${sample.audioData.size} bytes")
        
        currentJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                when {
                    sample.fileUri.isNotBlank() -> {
                        Log.d(TAG, "Using fileUri")
                        playFromUri(sample.fileUri)
                    }
                    sample.audioData.isNotEmpty() -> {
                        Log.d(TAG, "Using audioData")
                        playFromAudioData(sample.audioData)
                    }
                    else -> {
                        Log.e(TAG, "No audio source available for: ${sample.name}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Playback error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Stops any currently playing preview.
     */
    fun stop() {
        cancelCurrentPlayback()
    }
    
    /**
     * Cleans up resources.
     */
    fun release() {
        cancelCurrentPlayback()
    }
    
    // ========== PRIVATE HELPERS ==========
    
    private suspend fun playFromUri(uriString: String) {
        withContext(Dispatchers.IO) {
            try {
                currentPlayer = MediaPlayer().apply {
                    setDataSource(uriString)
                    prepare()
                    start()
                    
                    setOnCompletionListener {
                        Log.d(TAG, "URI playback completed normally")
                        cleanupPlayback()
                    }
                    
                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                        cleanupPlayback()
                        false
                    }
                }
                Log.d(TAG, "MediaPlayer started for URI")
                setupTimeout()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play from URI: ${e.message}")
                e.printStackTrace()
                cleanupPlayback()
            }
        }
    }
    
    private suspend fun playFromAudioData(audioData: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                val tempFile = File.createTempFile("preview_", ".wav", context.cacheDir)
                tempFile.deleteOnExit()
                tempFile.writeBytes(audioData)
                Log.d(TAG, "Created temp file: ${tempFile.absolutePath}")
                
                currentPlayer = MediaPlayer().apply {
                    setDataSource(tempFile.absolutePath)
                    prepare()
                    start()
                    
                    setOnCompletionListener {
                        tempFile.delete()
                        Log.d(TAG, "AudioData playback completed normally")
                        cleanupPlayback()
                    }
                    
                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                        tempFile.delete()
                        cleanupPlayback()
                        false
                    }
                }
                Log.d(TAG, "MediaPlayer started for audioData")
                setupTimeout()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play from audioData: ${e.message}")
                e.printStackTrace()
                cleanupPlayback()
            }
        }
    }
    
    private fun setupTimeout() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(PREVIEW_TIMEOUT_MS)
            currentPlayer?.let {
                if (it.isPlaying) {
                    Log.d(TAG, "Stopping playback after timeout")
                    it.stop()
                }
            }
            cleanupPlayback()
        }
    }
    
    private fun cleanupPlayback() {
        currentPlayer?.release()
        currentPlayer = null
        currentJob?.cancel()
        currentJob = null
        Log.d(TAG, "Playback resources cleaned up")
    }
    
    private fun cancelCurrentPlayback() {
        currentPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                Log.d(TAG, "Playback stopped manually")
            }
            it.release()
        }
        currentPlayer = null
        currentJob?.cancel()
        currentJob = null
    }
}