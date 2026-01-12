package bbb.audio.syntAX1.data.repository

import org.puredata.core.PdBase

class SampleScrubberRepository {

    fun setChunkSize(sizeMs: Float) {
        PdBase.sendFloat("scrubber-chunk-size", sizeMs)
    }

    fun setReadPoint(pointMs: Float) {
        PdBase.sendFloat("scrubber-read-point", pointMs)
    }

    fun setTransposition(cents: Float) {
        // cents to semitones: cents / 100
        PdBase.sendFloat("scrubber-transposition", cents / 100f)
    }

    fun setSpeedRatio(ratio: Float) {
        PdBase.sendFloat("scrubber-speed-ratio", ratio)
    }

    fun setVolume(vol: Float) {
        PdBase.sendFloat("scrubber-volume", vol.coerceIn(0f, 1f))
    }

    fun record() {
        PdBase.sendFloat("scrubber-record", 1f)  // Bang!
    }

    fun reset() {
        PdBase.sendFloat("scrubber-reset", 1f)
    }

    fun toggleMute(mute: Boolean) {
        PdBase.sendFloat("scrubber-mute", if (mute) 1f else 0f)
    }
}