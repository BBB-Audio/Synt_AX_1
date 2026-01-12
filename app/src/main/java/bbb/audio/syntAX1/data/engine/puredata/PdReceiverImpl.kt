/*
 * Copyright 2026 Dirk Hammacher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package bbb.audio.syntAX1.data.engine.puredata

import android.util.Log
import bbb.audio.syntAX1.data.engine.midi.MidiEvent
import bbb.audio.syntAX1.data.engine.midi.MidiEventPool
import bbb.audio.syntAX1.data.engine.midi.MidiEventType
import bbb.audio.syntAX1.data.repository.SynthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.puredata.core.PdReceiver

/**
 * Implements the PdReceiver to receive messages from Pure Data.
 * This is the ONLY entry point for data FROM PD to Kotlin.
 *
 * IMPORTANT: This is instantiated BEFORE SynthRepository, to avoid
 * circular dependencies (PdEngineManager → SynthRepository → ... → PdEngineManager).
 * The SynthRepository is set later via setSynthRepository().
 *
 * Responsibilities:
 * 1. Receive clock ticks from pd_clock.pd via "android_step_number"
 * 2. Emit ticks to SequencerViewModel
 * 3. Handle external MIDI events
 * 4. Dispatch MIDI events to SynthRepository
 * 5. Handle debug/status messages from PD
 */
class PdReceiverImpl(
    private val midiEventPool: MidiEventPool,
    private var synthRepository: SynthRepository?,
    private val pdEngineManager: PdEngineManager
) : PdReceiver {

    private val _midiActivityFlow = MutableSharedFlow<Boolean>(replay = 1)
    val midiActivityFlow: SharedFlow<Boolean> get() = _midiActivityFlow

//    private val _beatIndicatorFlow = MutableSharedFlow<Boolean>(replay = 1)
//    val beatIndicatorFlow: SharedFlow<Boolean> get() = _beatIndicatorFlow
//    private var stepCounter = 0

    /**
     * Called after creation to set the SynthRepository reference.
     *
     * PdEngineManager → PdReceiverImpl; PdEngineManager → SynthRepository
     */
    fun setSynthRepository(repo: SynthRepository) {
        synthRepository = repo
        Log.d(TAG, "✓ SynthRepository reference set in PdReceiverImpl")
    }

    /**
     * Called when pd_clock.pd sends a float via [send android_step_number]
     * This is THE clock tick - the heartbeat of the sequencer
     *
     * pd_clock.pd sendet 0-15
     */
    override fun receiveFloat(source: String?, x: Float) {
        when (source) {
            "android_test_float" -> {
                Log.i(TAG, "🎉🎉🎉 BRIDGE TEST SUCCESS! Float received: $x")
            }
            "android_step_number" -> {
                val stepIndex = x.toInt()
                onClockTick(stepIndex, pdEngineManager.getCurrentBpm())
            }
//            "volume" -> {
//                Log.d(TAG, "Volume feedback from PD: $x")
//            }
            else -> {
                Log.d(TAG, "[PD Float] $source = $x")
            }
        }
    }

    /**
     * Handle a clock tick from pd_clock.pd
     * Poll the event pool and dispatch events
     */
    override fun receiveBang(source: String?) {
        when (source) {
//            "android_test_connection" -> {
//                Log.i(TAG, "✅ BRIDGE VERIFIED: Test bang received from PD!")
//            }
//            "clock_quarter" -> {
//                // Emit to beat indicator flow for SynthScreen
//                Log.i(TAG, "🔔 BEAT SIGNAL RECEIVED! Emitting to flow...")
//                _beatIndicatorFlow.tryEmit(true)
//                Log.d(TAG, "🎵 Quarter note beat for indicator")
//            }
//            else -> {
//                // Ignore other subdivisions for now
//                Log.v(TAG, "[PD Bang] from: $source (ignored)")
//            }
        }
    }
    fun onClockTick(stepIndex: Int, bpm: Float) {
        try {
            val poolSize = midiEventPool.size()
            //Log.d(TAG, "🕐 Clock tick START: step $stepIndex, pool size BEFORE: $poolSize")

            // 1. Poll the event pool for this step
            val events = midiEventPool.getEventsForStep(stepIndex)

            //Log.d(TAG, "Clock tick: step $stepIndex, ${events.size} events")
            //Log.d(TAG, "🕐 Pool size AFTER: ${midiEventPool.size()}")

            // 2. Dispatch each event
            for (event in events) {
                dispatchMidiEvent(event)
            }

            // 3. Emit MIDI activity if events received
            if (events.isNotEmpty()) {
                _midiActivityFlow.tryEmit(true)
            }
            if (events.isNotEmpty()) {
                try {
                    _midiActivityFlow.tryEmit(true)
                } catch (e: Exception) {
                    Log.e(TAG, "Error emitting MIDI activity: ${e.message}")
                }
            }

            // 4. Emit to ViewModel flow
            pdEngineManager.emitClockTick(stepIndex)

        } catch (e: Exception) {
            Log.e(TAG, "Error in onClockTick: ${e.message}", e)
        }
    }
    private fun handleClockTick(stepIndex: Int) {
        try {
            val poolSize = midiEventPool.size()
            //Log.i(TAG, "🕐 ════════════════════════════════════")
            //Log.i(TAG, "🕐 CLOCK TICK FROM pd_clock.pd: step $stepIndex (pool size: $poolSize)")
            //Log.i(TAG, "🕐 ════════════════════════════════════")

            // 1. Poll the event pool for this step
            val events = midiEventPool.getEventsForStep(stepIndex)

            // 2. Dispatch each event
            for (event in events) {
                dispatchMidiEvent(event)
            }

            // 3. Emit MIDI activity if events received
            if (events.isNotEmpty()) {
                try {
                    _midiActivityFlow.tryEmit(true)
                } catch (e: Exception) {
                    Log.e(TAG, "Error emitting MIDI activity: ${e.message}")
                }
            }

            // 4. Emit clock tick to SequencerViewModel
            pdEngineManager.emitClockTick(stepIndex)

        } catch (e: Exception) {
            Log.e(TAG, "Error in handleClockTick: ${e.message}", e)
            e.printStackTrace()
        }
    }

    /**
     * Dispatch a single MIDI event to the synth
     */
    private fun dispatchMidiEvent(event: MidiEvent) {
        try {
            if (synthRepository == null) {
                Log.w(TAG, "⚠️ SynthRepository is null! Cannot dispatch event: ${event.noteNumber}")
                return
            }

            when {
                event.isNoteOn() -> {
                    Log.d(TAG, "→ NoteOn: ${event.noteNumber} vel:${event.velocity} (${event.source})")
                    synthRepository?.playNote(event.noteNumber, event.velocity)
                }
                event.isNoteOff() -> {
                    Log.d(TAG, "→ NoteOff: ${event.noteNumber} (${event.source})")
                    synthRepository?.stopNote(event.noteNumber)
                }
                event.type == MidiEventType.CC -> {
                    handleControlChange(event)
                }
                event.type == MidiEventType.PITCH_BEND -> {
                    handlePitchBend(event)
                }
                event.type == MidiEventType.PROGRAM_CHANGE -> {
                    Log.d(TAG, "→ ProgramChange: ${event.velocity}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error dispatching MIDI event: ${e.message}", e)
        }
    }

    /**
     * Handle CC (Control Change) events
     */
    private fun handleControlChange(event: MidiEvent) {
        val ccNumber = event.noteNumber
        val ccValue = event.velocity / 127f

        Log.d(TAG, "→ CC #$ccNumber: $ccValue")

        when (ccNumber) {
            1 -> synthRepository?.setCutoff(ccValue)
            74 -> synthRepository?.setResonance(ccValue)
            7 -> synthRepository?.setMainVolume(ccValue)
            else -> Log.d(TAG, "Unmapped CC: $ccNumber")
        }
    }

    /**
     * Handle Pitch Bend events
     */
    private fun handlePitchBend(event: MidiEvent) {
        Log.d(TAG, "→ PitchBend: ${event.velocity}")
        // TODO: Implement pitch bend in synth
    }

    // ============ PdReceiver Interface Implementation ============

    override fun print(s: String?) {
        Log.d(TAG, "[PD] $s")
    }

//    override fun receiveBang(source: String?) {
//        Log.d(TAG, "[PD Bang] from: $source")
//    }

    override fun receiveSymbol(source: String?, symbol: String?) {
        Log.d(TAG, "[PD Symbol] $source: $symbol")
        if (source == "android_step_number") {
            Log.i(TAG, "📡 receiveSymbol called with android_step_number! symbol=$symbol")
            symbol?.toIntOrNull()?.let { stepIndex ->
                Log.i(TAG, "🔀 Received Step $stepIndex as SYMBOL (not float)")
                handleClockTick(stepIndex)
            }
        }
    }

    override fun receiveList(source: String?, vararg args: Any?) {
        Log.d(TAG, "[PD List] $source: ${args.joinToString(", ")}")
    }

    override fun receiveMessage(source: String?, symbol: String?, vararg args: Any?) {
        Log.d(TAG, "[PD Message] $symbol from $source: ${args.joinToString(", ")}")

        when (symbol) {
            "patch_loaded" -> Log.i(TAG, "✓ Patch loaded successfully")
            "error" -> Log.e(TAG, "✗ Patch error: ${args.joinToString()}")
            "synth_ready" -> Log.i(TAG, "✓ Synth initialized in PD")
        }
    }

    companion object {
        private const val TAG = "PdReceiverImpl"
    }
}