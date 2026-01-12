package bbb.audio.syntAX1.data.engine

import android.content.Context
import org.puredata.android.io.PdAudio

class AudioEngine(private val context: Context) {

    fun initAudio(
        sampleRate: Int = 44100,
        inChannels: Int = 0,
        outChannels: Int = 2,
        ticksPerBuffer: Int = 4,
        restart: Boolean = false
    ) {
        PdAudio.initAudio(sampleRate, inChannels, outChannels, ticksPerBuffer, restart)
    }

    fun start() {
        PdAudio.startAudio(context)
    }

    fun stop() {
        PdAudio.stopAudio()
    }

    fun release() {
        PdAudio.release()
    }
}
