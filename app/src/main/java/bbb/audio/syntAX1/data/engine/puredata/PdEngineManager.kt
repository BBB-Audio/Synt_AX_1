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

import android.content.Context
import android.util.Log
import bbb.audio.syntAX1.data.engine.AudioEngine
import bbb.audio.syntAX1.data.engine.midi.ExternalMidiListener
import bbb.audio.syntAX1.data.engine.midi.MidiEventPool
import bbb.audio.syntAX1.data.repository.SamplerRepository
import bbb.audio.syntAX1.data.repository.SynthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.puredata.android.utils.PdUiDispatcher
import org.puredata.core.PdBase

/**
 * Core manager for Pure Data audio engine and MIDI system.
 *
 * Responsibilities:
 * - Initialize and manage PD audio engine
 * - Load and manage PD patches
 * - Handle MIDI event pooling and routing
 * - Manage communication between Kotlin and Pure Data
 * - Provide clock synchronization for sequencer
 *
 * @param context Android context for resource access
 */
class PdEngineManager(private val context: Context) {

//region Properties
    private val audioEngine = AudioEngine(context)
    private val patchLoader = PdPatchLoader(context)
    private val midiEventPool = MidiEventPool()
    private val pdUiDispatcher = PdUiDispatcher()
    private var externalMidiListener: ExternalMidiListener? = null
    private var pdReceiver: PdReceiverImpl? = null
    private var synthRepository: SynthRepository? = null
    private var samplerRepository: SamplerRepository? = null

    // Flow for clock ticks (0-15 steps)
    private val _clockTickFlow = MutableSharedFlow<Int>(replay = 1)
    val clockTickFlow: SharedFlow<Int> = _clockTickFlow.asSharedFlow()

    // Flow for MIDI activity indicator
    val midiActivityFlow: SharedFlow<Boolean>
        get() = pdReceiver?.midiActivityFlow ?: MutableSharedFlow()

//region Temporary commented out for Präsentation
//
// Flow for beat indicator (quarter notes)
//    val beatIndicatorFlow: SharedFlow<Boolean>
//        get() = pdReceiver?.beatIndicatorFlow ?: MutableSharedFlow()
//endregion
    private var clockIsRunning = false
    private var isInitialized = false
    private var currentBpm = 120f
    private var lastLoadedPatchResId: Int? = null
    private var lastLoadedPatchFileName: String? = null
    private var lastLoadedWavFiles: List<Pair<Int, String>> = emptyList()
//endregion

    /**
     * Initializes the audio engine, MIDI system, and patch loader.
     *
     * @param sampleRate Audio sample rate (default: 44100)
     * @param inChannels Number of input channels (default: 0)
     * @param outChannels Number of output channels (default: 2)
     * @param ticksPerBuffer PD ticks per buffer (default: 4)
     * @param restart Whether to restart if already initialized
     * @return Boolean indicating success
     */
    fun initialize(
        sampleRate: Int = 44100,
        inChannels: Int = 0,
        outChannels: Int = 2,
        ticksPerBuffer: Int = 4,
        restart: Boolean = false

    ): Boolean {
        return try {
            if (isInitialized && !restart) {
                Log.w(TAG, "Engine already initialized")
                return true
            }

            audioEngine.initAudio(sampleRate, inChannels, outChannels, ticksPerBuffer, restart)
            Log.d(TAG, "Audio initialized: $sampleRate Hz, in=$inChannels, out=$outChannels")

            synthRepository = SynthRepository(this)
            Log.d(TAG, "SynthRepository created")

            setupExternalMidiListener()

            samplerRepository = SamplerRepository()
            Log.d(TAG, "SamplerRepository created")

            isInitialized = true
            Log.i(TAG, "PdEngineManager initialization complete")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize engine: ${e.message}", e)
            false
        }
    }

    /**
     * Loads a PD patch with optional dependent resources.
     *
     * @param patchRawResId Resource ID of the patch file
     * @param patchFileName Name of the patch file
     * @param dependentWavFiles List of dependent WAV files
     * @param dependentPatches List of dependent sub-patches
     * @return Boolean indicating success
     */
    fun loadPatch(
        patchRawResId: Int,
        patchFileName: String,
        dependentWavFiles: List<Pair<Int, String>> = emptyList(),
        dependentPatches: List<Pair<Int, String>> = emptyList()
    ): Boolean {
        if (!isInitialized) {
            Log.e(TAG, "Engine not initialized! Call initialize() first")
            return false
        }

        return try {
            dependentPatches.forEach { (resId, fileName) ->
                patchLoader.loadPatch(resId, fileName, emptyList())
            }

            val result = patchLoader.loadPatch(patchRawResId, patchFileName, dependentWavFiles)
            if (result) Log.i(TAG, "Patch '$patchFileName' loaded successfully")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading patch: ${e.message}", e)
            false
        }
    }

    /**
     * Sets up the PD receiver for communication from Pure Data.
     * Must be called after patches are loaded.
     *
     * @return Boolean indicating success
     */
    fun setupReceiver(): Boolean {
        return try {
            if (pdReceiver == null) setupPdReceiver()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup receiver: ${e.message}", e)
            false
        }
    }

    /**
     * Starts the PD audio engine.
     *
     * @return Boolean indicating success
     */
    fun startAudio(): Boolean {
        return try {
            audioEngine.start()
            Log.d(TAG, "Audio engine started")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start audio: ${e.message}", e)
            false
        }
    }

    /**
     * Starts the PD clock for sequencer.
     *
     * @return Boolean indicating success
     */
    fun startClock(): Boolean {
        return try {
            sendBangToPd("start")
            clockIsRunning = true
            Log.d(TAG, "Clock started")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start clock: ${e.message}", e)
            false
        }
    }

    /**
     * Stops the PD clock and clears pending MIDI events.
     */
    fun stopClock() {
        try {
            sendBangToPd("stop")
            clockIsRunning = false
            midiEventPool.clear()
            synthRepository?.stopAllNotes()
            synthRepository?.clearAllNotes()
            Log.d(TAG, "Clock stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping clock: ${e.message}", e)
        }
    }

    /**
     * Stops audio and clock.
     */

    fun stop() {
        stopClock()
        audioEngine.stop()

    }

    /**
     * Releases all audio resources.
     */
    fun releaseAudioEngine() {
        try {
            stop()
            audioEngine.release()
            externalMidiListener?.releaseMidi()
            samplerRepository = null
            isInitialized = false
            Log.d(TAG, "Engine released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing engine: ${e.message}", e)
        }
    }

    /**
     * Sets the BPM for the PD clock.
     *
     * @param bpm Beats per minute
     */
    fun setClockBpm(bpm: Float) {
        currentBpm = bpm
        sendToPd("set_bpm", bpm)
        Log.d(TAG, "Clock BPM set to: $bpm")
        val speedMultiplier = samplerRepository?.calculateSpeedForBpm(bpm, 120f)
        if (speedMultiplier != null) {
            samplerRepository?.setLoopSpeed(speedMultiplier)
            Log.d(TAG, "Sampler speed synced to BPM: $bpm")
        }

        Log.d(TAG, "Clock BPM set to: $bpm")
    }

    /**
     * Gets the current BPM.
     *
     * @return Current BPM value
     */
    fun getCurrentBpm(): Float = currentBpm

    /**
     * Checks if the clock is running.
     *
     * @return Boolean indicating clock status
     */
    fun isClockRunning(): Boolean = clockIsRunning

    /**
     * Sends a float value to a PD receiver.
     *
     * @param receiverName PD receiver symbol
     * @param value Float value to send
     */
    fun sendToPd(receiverName: String, value: Float) {
        try {
            PdBase.sendFloat(receiverName, value)
            Log.v(TAG, "→ PD [$receiverName]: $value")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending to PD: ${e.message}")
        }
    }

    /**
     * Sends a bang to a PD receiver.
     *
     * @param receiverName PD receiver symbol
     */
    fun sendBangToPd(receiverName: String) {
        try {
            PdBase.sendBang(receiverName)
            Log.v(TAG, "→ PD [$receiverName]: bang")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending bang to PD: ${e.message}")
        }
    }

    /**
     * Sends a list of values to a PD receiver.
     *
     * @param receiverName PD receiver symbol
     * @param values Float values to send
     */
    fun sendListToPd(receiverName: String, vararg values: Float) {
        try {
            val pdArgs = values.map { it as Any? }.toTypedArray()
            PdBase.sendList(receiverName, *pdArgs)
            Log.v(TAG, "→ PD [$receiverName]: ${values.joinToString()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending list to PD: ${e.message}")
        }
    }

    /**
     * Internal method to emit clock ticks to the flow
     *
     * @param stepIndex
     */
    internal fun emitClockTick(stepIndex: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            _clockTickFlow.emit(stepIndex)
        }
    }
    private fun setupPdReceiver() {
        try {
            Log.d(TAG, "Setting up PdReceiver via PdUiDispatcher...")

            pdReceiver = PdReceiverImpl(
                midiEventPool = midiEventPool,
                synthRepository = synthRepository!!,
                pdEngineManager = this
            )


            val symbolsToListen = listOf(
                "android_step_number",      // Main sequencer steps (0-15)
//                "clock_whole"
//                "clock_quarter",            // For beat indicator in SynthScreen
//                "android_test_connection"   // For bridge verification
            )

            symbolsToListen.forEach { symbol ->
                pdUiDispatcher.addListener(symbol, pdReceiver)
                Log.d(TAG, "Registered listener for symbol: '$symbol'")
            }

            Log.i(TAG, "PdReceiverImpl registered for ${symbolsToListen.size} PD symbols")
            PdBase.setReceiver(pdUiDispatcher)
            Log.i(TAG, "PdUiDispatcher set as global PD receiver")

// Test the bridge
//            PdBase.sendBang("android_test_connection")
//            Log.d(TAG, "Sent test bang for bridge verification")

        } catch (e: Exception) {
            Log.e(TAG, "ERROR in setupPdReceiver: ${e.message}", e)
        }
    }

//region Temporary commented out for Future Update

    /**
     * Enqueues a MIDI event from the sequencer UI.
     *
//     * @param event MIDI event to enqueue
     */
//    fun enqueueSequencerEvent(event: MidiEvent) {
//        midiEventPool.enqueue(event)
//    }
//
//    /**
//     * Gets the MIDI event pool for direct access.
//     *
//     * @return MidiEventPool instance
//     */
//    fun getMidiEventPool() = midiEventPool
//
//    /**
//     * Get available MIDI devices from the engine manager.
//     */
//    fun getAvailableMidiDevices(): List<String> {
//        return externalMidiListener?.getAvailableMidiDevices() ?: emptyList()
//    }
//endregion

    private fun setupExternalMidiListener() {
        try {
            externalMidiListener = ExternalMidiListener(
                context = context,
                midiEventPool = midiEventPool,
                pdEngineManager = this,
                quantizeToGrid = true
            )

            if (externalMidiListener?.initialize() != true) {
                Log.w(TAG, "USB MIDI not available (will continue without it)")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not initialize external MIDI: ${e.message}")
        }
    }
    fun getLastLoadedPatch(): Pair<Int, String>? {
        return if (lastLoadedPatchResId != null && lastLoadedPatchFileName != null) {
            Pair(lastLoadedPatchResId!!, lastLoadedPatchFileName!!)
        } else {
            null
        }
    }
    fun getLastLoadedWavFiles(): List<Pair<Int, String>> {
        return lastLoadedWavFiles
    }
    companion object {
        private const val TAG = "PdEngineManager"
    }
}