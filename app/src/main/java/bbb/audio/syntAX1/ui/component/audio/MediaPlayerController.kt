package bbb.audio.syntAX1.ui.component.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri

/**
 * Central controller for audio preview playback.
 * Can be used by both FreesoundDialog and SamplerScreen.
 */
class MediaPlayerController(
    private val context: Context,
    private val onCompletion: () -> Unit
    ) {
    private var mediaPlayer: MediaPlayer? = null

    fun playPreview(url: String?) {
        stop() // Stop any currently playing preview

        if (url.isNullOrEmpty()) {
            showToast("No preview available")
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(context, url.toUri())
                prepareAsync()
                setOnPreparedListener { start() }
                setOnCompletionListener {
                    onCompletion()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("MediaPlayer", "Error: what=$what, extra=$extra")
                    false
                }
            }
            Log.d("MediaPlayer", "Playing preview: $url")
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error: ${e.message}")
            showToast("Preview failed to play")
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}