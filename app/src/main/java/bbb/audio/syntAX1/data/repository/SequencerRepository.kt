package bbb.audio.syntAX1.data.repository

import android.util.Log
import bbb.audio.syntAX1.data.engine.puredata.PdEngineManager
import bbb.audio.syntAX1.data.model.Step
import kotlinx.coroutines.flow.SharedFlow

/**
 * SequencerRepository - Façade for all Sequencer operations
 *
 * Responsibilities:
 * - Provides clock tick flow to ViewModel
 * - Handles step triggering logic (business logic)
 * - Manages playback control (start/stop/reset)
 * - BPM control
 *
 * Dependencies:
 * - PdEngineManager (clock, patch communication)
 * - SynthRepository (note playback)
 */
class SequencerRepository(
    private val pdEngineManager: PdEngineManager,
    private val synthRepository: SynthRepository
) {
    private var currentPlayingNote: Int? = null
    /**
     * Get the clock tick flow
     * Emits step index (0-15) whenever a new step should trigger
     */
    fun getClockTickFlow(): SharedFlow<Int> {
        return pdEngineManager.clockTickFlow
    }

    /**
     * Core business logic: Handle a step tick
     *
     * When the clock ticks:
     * 1. Check if the step is enabled (ON)
     * 2. If yes, play the note with velocity
     * 3. If no, do nothing
     */
    fun handleSequencerTick(step: Step) {
        // Stop previous note if any
//        currentPlayingNote?.let { previousNote ->
//            synthRepository.stopNote(previousNote)
//            Log.d(TAG, "⏹️ Stopped previous note: $previousNote")
//        }
        if (step.isOn) {
            val velocity = (step.veloKnobValue * 127).toInt().coerceIn(0, 127)
            //Log.d(TAG, "▶️ Step ${step.id} ON - Playing note ${step.notePitch} vel=$velocity")
            synthRepository.playNote(step.notePitch.toInt(), velocity)
            currentPlayingNote = step.notePitch.toInt()
        } else {
            //Log.d(TAG, "⏸️ Step ${step.id} OFF - No note")
        }
    }

    /**
     * Playback Control
     */
    fun startClock(): Boolean {
        return pdEngineManager.startClock()
    }

    fun stopClock() {
        pdEngineManager.stopClock()
        synthRepository.stopAllNotes()
    }

    fun isClockRunning(): Boolean {
        return pdEngineManager.isClockRunning()
    }

    /**
     * BPM Control
     */
    fun setBpm(bpm: Float) {
        pdEngineManager.setClockBpm(bpm)
    }

    fun getBpm(): Float {
        return pdEngineManager.getCurrentBpm()
    }

    /**
     * Panic - Stop all notes
     */
    fun panic() {
        for (note in 0..127) {
            currentPlayingNote?.let { synthRepository.stopNote(it) }
            currentPlayingNote = null
            synthRepository.stopNote(note)
        }
        Log.d(TAG, "Panic: All Notes Off sent")
    }

    companion object {
        private const val TAG = "SequencerRepository"
    }
}