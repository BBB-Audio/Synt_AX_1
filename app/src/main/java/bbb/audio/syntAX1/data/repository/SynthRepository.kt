package bbb.audio.syntAX1.data.repository

import android.util.Log
import bbb.audio.syntAX1.data.engine.puredata.PdEngineManager
import bbb.audio.syntAX1.ui.viewmodel.FilterType
import kotlinx.coroutines.flow.SharedFlow

/**
 * SynthRepository
 *
 * Provides high-level synth control methods.
 * All communication with PD goes through PdEngineManager.
 */
class SynthRepository(
    private val pdEngineManager: PdEngineManager
) {
    val midiActivityFlow: SharedFlow<Boolean> = pdEngineManager.midiActivityFlow
//    val beatIndicatorFlow: SharedFlow<Boolean> = pdEngineManager.beatIndicatorFlow

    /**
     * Sets the main output volume of the synth
     *
     * @param volume Volume level (0.0f = silent, 1.0f = maximum)
     */
    fun setMainVolume(volume: Float) {
        try {
            val validVolume = volume.coerceIn(0f, 1f)
            pdEngineManager.sendToPd("set_volume", validVolume)
            Log.d(TAG, "setMainVolume: $validVolume")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting volume: ${e.message}")
        }
    }

    fun setSubVolume(subVolume: Float) {
        try {
            val normalizedValue = subVolume.coerceIn(0f, 1f)
            val pdValue = normalizedValue * 8f
            pdEngineManager.sendToPd("set_sub", pdValue)
            Log.d(TAG, "setSubVolume: UI=$normalizedValue -> PD=$pdValue")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting SubVolume: ${e.message}")
        }
    }

//region Note
    /**
     * Plays a note with given pitch and velocity
     *
     * @param noteNumber The MIDI note number (0-127, e.g., 60 for Middle C)
     * @param velocity The velocity (0-127)
     */
    fun playNote(noteNumber: Int, velocity: Int) {
        try {
            val validNote = noteNumber.coerceIn(0, 127)
            val validVel = velocity.coerceIn(0, 127)

            pdEngineManager.sendListToPd("notein", validNote.toFloat(), validVel.toFloat())
            Log.d(TAG, "playNote: note=$validNote vel=$validVel")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing note: ${e.message}")
        }
    }

    /**
     * Stops a note with given pitch
     * Sends velocity 0 (Note Off)
     *
     * @param noteNumber The MIDI note number to stop (0-127)
     */
    fun stopNote(noteNumber: Int) {
        try {
            val validNote = noteNumber.coerceIn(0, 127)
            pdEngineManager.sendToPd("stop_notes", validNote.toFloat())
            Log.d(TAG, "stopNote: note=$validNote")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping note: ${e.message}")
        }
    }
    fun stopAllNotes() {
        try {
            pdEngineManager.sendBangToPd("stop_notes") }
        catch (e: Exception) {
            Log.e(TAG, "Error stopping all notes: ${e.message}")
        }
    }

    fun clearAllNotes() {
        try {
            pdEngineManager.sendBangToPd("clear_notes")
            Log.d(TAG, "clearAllNotes")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all notes: ${e.message}")
        }
    }

//endregion

//region Filter
    /**
     * Sets the filter cutoff frequency
     *
     * @param value Filter cutoff (0.0 = fully closed, 1.0 = fully open)
     */
    fun setCutoff(value: Float) {
        try {
            val normalizedValue = value.coerceIn(0f, 1f)
            val exponentialValue = normalizedValue * normalizedValue * normalizedValue * normalizedValue
            val cutoffHz = exponentialValue * 10000f + 20f

            pdEngineManager.sendToPd("set_cutoff", cutoffHz)
            Log.d(TAG, "setCutoff: UI=$normalizedValue -> PD=${cutoffHz}Hz")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting cutoff: ${e.message}")
        }
    }

    /**
     * Sets the filter resonance (Q factor)
     *
     * @param value Resonance (0.0 = no resonance, 1.0 = maximum)
     */
    fun setResonance(value: Float) {
        try {
            val normalizedValue = value.coerceIn(0f, 1f)
            val resoValue = normalizedValue * 100f

            pdEngineManager.sendToPd("set_reso", resoValue)
            Log.d(TAG, "setResonance: UI=$normalizedValue -> PD=$resoValue")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting resonance: ${e.message}")
        }
    }

    /**
     * Sets the filter type
     *
     * @param type LOW_PASS or HIGH_PASS
     */
    fun setFilterType(type: FilterType) {
        try {
            val pdValue = if (type == FilterType.LOW_PASS) 0f else 1f
            pdEngineManager.sendToPd("filter-type", pdValue)
            Log.d(TAG, "setFilterType: $type")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting filter type: ${e.message}")
        }
    }
//endregion

//region Envelope
    /**
     * Sets the envelope attack time.
     * Converts a normalized UI value (0.0-1.0) to a millisecond range (e.g., 1ms-3000ms).
     *
     * @param value Normalized value from the UI slider (0.0 to 1.0).
     */
    fun setAttack(value: Float) {
        try {
            val normalizedValue = value.coerceIn(0f, 1f)
            val attackMs = 1f + (normalizedValue * normalizedValue) * 2999f
            pdEngineManager.sendToPd("set_attack", attackMs)
            Log.d(TAG, "setAttack: UI=$normalizedValue -> ${attackMs}ms")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting attack: ${e.message}")
        }
    }

    /**
     * Sets the envelope decay time.
     * Converts a normalized UI value (0.0-1.0) to a millisecond range.
     *
     * @param value Normalized value from the UI slider (0.0 to 1.0).
     */
    fun setDecay(value: Float) {
        try {
            val normalizedValue = value.coerceIn(0f, 1f)
            val decayMs = 1f + (normalizedValue * normalizedValue) * 4999f
            pdEngineManager.sendToPd("set_decay", decayMs)
            Log.d(TAG, "setDecay: UI=$normalizedValue -> ${decayMs}ms")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting decay: ${e.message}")
        }
    }

    /**
     * Sets the envelope sustain level.
     * Sustain is a level (0-1), not a time, so we just pass it through.
     *
     * @param value Sustain level (0.0 to 1.0).
     */
    fun setSustain(value: Float) {
        try {
            val validValue = value.coerceIn(0f, 1f)
            pdEngineManager.sendToPd("set_sustain", validValue)
            Log.d(TAG, "setSustain: $validValue")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting sustain: ${e.message}")
        }
    }

    /**
     * Sets the envelope release time.
     * Converts a normalized UI value (0.0-1.0) to a millisecond range.
     *
     * @param value Normalized value from the UI slider (0.0 to 1.0).
     */
    fun setRelease(value: Float) {
        try {
            val normalizedValue = value.coerceIn(0f, 1f)
            val releaseMs = 5f + (normalizedValue * normalizedValue) * 1000f
            pdEngineManager.sendToPd("set_release", releaseMs)
            Log.d(TAG, "setRelease: UI=$normalizedValue -> ${releaseMs}ms")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting release: ${e.message}")
        }
    }
//endregion

//region Chorus
    fun setChorusDepth(depth: Float) {
        try {
            val validDepth = depth.coerceIn(0f, 1f)
            pdEngineManager.sendToPd("chorus", validDepth)
            Log.d(TAG, "setChorusDepth: $validDepth")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting chorus depth: ${e.message}")
        }
    }

    fun setLfoSpeed(speed: Float) {
        try {
            val normalizedValue = speed.coerceIn(0f, 1f)
            val lfoHz = normalizedValue * 20f
            pdEngineManager.sendToPd("lfo_freq", lfoHz)
            Log.d(TAG, "setLfoSpeed: UI=$normalizedValue -> ${lfoHz}Hz")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting LFO speed: ${e.message}")
        }
    }
//endregion

//region Delay
fun setDelayAmount(amount: Float) {
    try {
        val normalizedValue = amount.coerceIn(0f, 1f)
        val delayAmountValue = normalizedValue * 5f
        pdEngineManager.sendToPd("set_delayamount", delayAmountValue)
        Log.d(TAG, "setDelayAmount: UI=$normalizedValue -> PD=$delayAmountValue")
    } catch (e: Exception) {
        Log.e(TAG, "Error setting delay amount: ${e.message}")
    }
}

    fun setDelayTime(time: Float) {
        try {
            val normalizedValue = time.coerceIn(0f, 1f)
            val delayTimeValue = normalizedValue * 1000f
            pdEngineManager.sendToPd("set_delaytime", delayTimeValue)
            Log.d(TAG, "setDelayTime: UI=$normalizedValue -> PD=$delayTimeValue")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting delay time: ${e.message}")
        }
    }

    fun setDelayFeed(feed: Float) {
        try {
            val validFeed = feed.coerceIn(0f, 1f)
            pdEngineManager.sendToPd("set_delayfeed", validFeed)
            Log.d(TAG, "setDelayFeed: $validFeed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting delay feed: ${e.message}")
        }
    }
    //endregion

//region Reverb
    fun setReverbDepth(value: Float) {
        try {
            val normalizedValue = value.coerceIn(0f, 1f)
            // Scale to 0-100 range
            val depthValue = normalizedValue * 100f
            pdEngineManager.sendToPd("set_reverb", depthValue)
            Log.d(TAG, "setReverbDepth: UI=$normalizedValue -> PD=$depthValue")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting reverb depth: ${e.message}")
        }
    }

    fun setReverbFeed(value: Float) {
        try {
            val normalizedValue = value.coerceIn(0f, 1f)
            // Scale to 0-1000 range
            val feedValue = normalizedValue * 1000f
            pdEngineManager.sendToPd("set_feed", feedValue)
            Log.d(TAG, "setReverbFeed: UI=$normalizedValue -> PD=$feedValue")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting reverb feed: ${e.message}")
        }
    }

    fun setReverbCrossfreq(value: Float) {
        try {
            val normalizedValue = value.coerceIn(0f, 1f)
            // Scale to 0-10000 range
            val crossValue = normalizedValue * 10000f
            pdEngineManager.sendToPd("set_crossfreq", crossValue)
            Log.d(TAG, "setReverbCrossfreq: UI=$normalizedValue -> PD=$crossValue")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting reverb cross-frequency: ${e.message}")
        }
    }

    fun setReverbDamp(value: Float) {
        try {
            val normalizedValue = value.coerceIn(0f, 1f)
            // Scale to 0-100 range
            val dampValue = normalizedValue * 100f
            pdEngineManager.sendToPd("set_damp", dampValue)
            Log.d(TAG, "setReverbDamp: UI=$normalizedValue -> PD=$dampValue")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting reverb damp: ${e.message}")
        }
    }
//endregion
    companion object {
        private const val TAG = "SynthRepository"
    }
}