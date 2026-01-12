package bbb.audio.syntAX1.data.engine.midi

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.media.midi.MidiOutputPort
import android.media.midi.MidiReceiver
import android.util.Log
import bbb.audio.syntAX1.data.engine.puredata.PdEngineManager

/**
 * ExternalMidiListener
 *
 * Listens to external USB MIDI devices via Android's MidiManager.
 * Routes incoming MIDI events to the MidiEventPool with proper quantization.
 *
 * ⚠️ Important: This uses the asynchronous MidiManager.openDevice() API
 * (not the deprecated synchronous approach).
 *
 * **MIDI Port Confusion (it's weird):**
 * - To RECEIVE MIDI: Use `device.openOutputPort()` ← Yes, really!
 * - To SEND MIDI: Use `device.openInputPort()`
 *
 * This is from MIDI's perspective (DIN MIDI), not Android's.
 * It's confusing but standardized.
 *
 * @param context Android context
 * @param midiEventPool Queue for dispatching events
 * @param pdEngineManager Reference to PdEngineManager for BPM + step info
 * @param quantizeToGrid If true, snap external MIDI to sequencer steps
 */
class ExternalMidiListener(
    private val context: Context,
    private val midiEventPool: MidiEventPool,
    private val pdEngineManager: PdEngineManager,
    private val quantizeToGrid: Boolean = true
) {

    private val midiManager: MidiManager? =
        context.getSystemService(Context.MIDI_SERVICE) as? MidiManager
    private var midiOutputPort: MidiOutputPort? = null
    private var midiDevice: MidiDevice? = null

    private val clockStartTime = System.currentTimeMillis()

    /**
     * Get list of available MIDI devices
     */
    fun getAvailableMidiDevices(): List<String> {
        return try {
            if (midiManager == null) return emptyList()

            midiManager.devices
                .filter { it.outputPortCount > 0 }
                .mapNotNull {
                    it.properties?.getString(MidiDeviceInfo.PROPERTY_NAME)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting MIDI devices: ${e.message}")
            emptyList()
        }
    }


    /**
     * Initialize and open the MIDI input port asynchronously.
     *
     * This searches for available MIDI devices, then opens the first one
     * that has output ports (in MIDI terminology = can send to us).
     *
     * @return Boolean: true if initialization started successfully, false otherwise
     */
    fun initialize(): Boolean {
        return try {
            if (midiManager == null) {
                Log.w(TAG, "MidiManager not available on this device")
                return false
            }

            // 1. Find available MIDI devices
            val devices = midiManager.devices
            if (devices.isEmpty()) {
                Log.w(TAG, "No MIDI devices found")
                return false
            }

            // 2. Find first device with output ports (i.e., can send to App)
            val outputDevice = devices.find {
                it.outputPortCount > 0
            }

            if (outputDevice == null) {
                Log.w(TAG, "No MIDI output devices found (devices that can send to us)")
                return false
            }

            // 3. Open the device asynchronously
            midiManager.openDevice(
                outputDevice,
                { device ->
                    // onDeviceOpened callback
                    if (device != null) {
                        onMidiDeviceOpened(device)
                    } else {
                        Log.e(TAG, "Failed to open MIDI device")
                    }
                },
                null  // Handler: null = don't run on UI thread
            )

            Log.i(TAG, "✓ Opening MIDI device: ${outputDevice.properties?.getString(MidiDeviceInfo.PROPERTY_NAME) ?: "Unknown"}")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize USB MIDI listener: ${e.message}", e)
            false
        }
    }

    /**
     * Called when the MIDI device is successfully opened.
     *
     * At this point, we can access the device's ports.
     */
    private fun onMidiDeviceOpened(device: MidiDevice) {
        try {
            midiDevice = device

            this.midiOutputPort = try {
                // Open the first output port ("Output" = device sends to us = we RECEIVE)
                device.openOutputPort(0)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open output port: ${e.message}", e)

                releaseMidi()
                return
            }

            if (this.midiOutputPort == null) {
                Log.e(TAG, "MidiManager returned a null output port, even without an exception.")
                releaseMidi()
                return
            }

            val midiReceiver = MidiReceiverImpl(this)
            this.midiOutputPort?.connect(midiReceiver)

            Log.i(TAG, "✓ USB MIDI device connected and listening")

        } catch (e: Exception) {
            Log.e(TAG, "Error processing opened MIDI device: ${e.message}", e)
            releaseMidi()
        }
    }

    /**
     * Called when MIDI data arrives from the device.
     *
     * This is invoked by MidiReceiverImpl.onSend(), which bridges
     * the Android MIDI callback to our logic.
     *
     * @param data Raw MIDI byte data (status, data1, data2)
     * @param offset Offset in the data array
     * @param count Number of bytes to process
     * @param timestamp Timestamp from MidiManager (nanoseconds since system boot)
     */
    fun onMidiReceived(data: ByteArray?, offset: Int, count: Int, timestamp: Long) {
        if (data == null || count < 1) return

        try {
            val status = data[offset].toInt() and 0xFF
            val statusType = status and 0xF0
            val channel = status and 0x0F

            when (statusType) {
                0x90 -> {  // Note On
                    if (count >= 3) {
                        val noteNumber = data[offset + 1].toInt() and 0x7F
                        val velocity = data[offset + 2].toInt() and 0x7F
                        handleNoteOn(noteNumber, velocity, channel, timestamp)
                    }
                }
                0x80 -> {  // Note Off
                    if (count >= 3) {
                        val noteNumber = data[offset + 1].toInt() and 0x7F
                        val velocity = data[offset + 2].toInt() and 0x7F
                        handleNoteOff(noteNumber, velocity, channel, timestamp)
                    }
                }
                0xB0 -> {  // Control Change
                    if (count >= 3) {
                        val ccNumber = data[offset + 1].toInt() and 0x7F
                        val ccValue = data[offset + 2].toInt() and 0x7F
                        handleControlChange(ccNumber, ccValue, channel, timestamp)
                    }
                }
                0xE0 -> {  // Pitch Bend
                    if (count >= 3) {
                        val lsb = data[offset + 1].toInt() and 0x7F
                        val msb = data[offset + 2].toInt() and 0x7F
                        handlePitchBend(lsb, msb, channel, timestamp)
                    }
                }
                0xC0 -> {  // Program Change
                    if (count >= 2) {
                        val program = data[offset + 1].toInt() and 0x7F
                        handleProgramChange(program, channel, timestamp)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing MIDI data: ${e.message}", e)
        }
    }

    /**
     * Handle MIDI Note On event.
     *
     * Creates a MidiEvent and enqueues it for dispatch on the appropriate step.
     */
    private fun handleNoteOn(noteNumber: Int, velocity: Int, channel: Int, timestamp: Long) {
        val stepIndex = calculateStepIndex(timestamp)

        val event = MidiEvent(
            noteNumber = noteNumber,
            velocity = velocity,
            stepIndex = stepIndex,
            timestamp = timestamp,
            type = MidiEventType.NOTE_ON,
            channel = channel,
            source = MidiSource.EXTERNAL_USB
        )

        midiEventPool.enqueue(event)
        Log.d(TAG, "↓ USB NoteOn: $noteNumber vel:$velocity ch:$channel → step $stepIndex")
    }

    /**
     * Handle MIDI Note Off event.
     *
     * Always sets velocity to 0 (per MIDI spec for explicit Note Off).
     */
    private fun handleNoteOff(noteNumber: Int, velocity: Int, channel: Int, timestamp: Long) {
        val stepIndex = calculateStepIndex(timestamp)

        val event = MidiEvent(
            noteNumber = noteNumber,
            velocity = 0,  // Always 0 for explicit Note Off
            stepIndex = stepIndex,
            timestamp = timestamp,
            type = MidiEventType.NOTE_OFF,
            channel = channel,
            source = MidiSource.EXTERNAL_USB
        )

        midiEventPool.enqueue(event)
        Log.d(TAG, "↓ USB NoteOff: $noteNumber ch:$channel → step $stepIndex")
    }

    /**
     * Handle MIDI Control Change (CC) event.
     *
     * CC number is stored in the noteNumber field, CC value in velocity field.
     */
    private fun handleControlChange(ccNumber: Int, ccValue: Int, channel: Int, timestamp: Long) {
        val stepIndex = calculateStepIndex(timestamp)

        val event = MidiEvent(
            noteNumber = ccNumber,           // CC# in noteNumber
            velocity = ccValue,              // CC value in velocity
            stepIndex = stepIndex,
            timestamp = timestamp,
            type = MidiEventType.CC,
            channel = channel,
            source = MidiSource.EXTERNAL_USB
        )

        midiEventPool.enqueue(event)
        Log.d(TAG, "↓ USB CC #$ccNumber: $ccValue ch:$channel → step $stepIndex")
    }

    /**
     * Handle MIDI Pitch Bend event.
     *
     * Combines LSB and MSB into a 14-bit value.
     */
    private fun handlePitchBend(lsb: Int, msb: Int, channel: Int, timestamp: Long) {
        val stepIndex = calculateStepIndex(timestamp)
        val value = ((msb shl 7) or lsb)  // Combine into 14-bit value

        val event = MidiEvent(
            noteNumber = 0,  // Pitch bend doesn't use note number
            velocity = value,
            stepIndex = stepIndex,
            timestamp = timestamp,
            type = MidiEventType.PITCH_BEND,
            channel = channel,
            source = MidiSource.EXTERNAL_USB
        )

        midiEventPool.enqueue(event)
        Log.d(TAG, "↓ USB PitchBend: $value ch:$channel → step $stepIndex")
    }

    /**
     * Handle MIDI Program Change event.
     *
     * Typically used for patch selection in synthesizers.
     */
    private fun handleProgramChange(program: Int, channel: Int, timestamp: Long) {
        val stepIndex = calculateStepIndex(timestamp)

        val event = MidiEvent(
            noteNumber = 0,
            velocity = program,
            stepIndex = stepIndex,
            timestamp = timestamp,
            type = MidiEventType.PROGRAM_CHANGE,
            channel = channel,
            source = MidiSource.EXTERNAL_USB
        )

        midiEventPool.enqueue(event)
        Log.d(TAG, "↓ USB ProgramChange: $program ch:$channel → step $stepIndex")
    }

    /**
     * Calculate which sequencer step this MIDI event should be assigned to.
     *
     * **If quantizeToGrid = true:**
     *   Events are snapped to the nearest 16th step based on elapsed time.
     *   This keeps external MIDI synchronized with the internal sequencer.
     *
     * **If quantizeToGrid = false:**
     *   Events are assigned to the current step (may arrive "late").
     *   More "live" feeling but less aligned with sequencer grid.
     *
     * @param eventTimestamp System time when event arrived
     * @return Step index (0-15) for event dispatch
     */
    private fun calculateStepIndex(eventTimestamp: Long): Int {
        if (!quantizeToGrid) {
            // No quantization: assign to current step
            // TODO: Get current step from pdEngineManager if available
            return 0  // Fallback to step 0
        }

        // Calculate step based on elapsed time since clock started
        val elapsedMs = (System.currentTimeMillis() - clockStartTime)
        val bpm = pdEngineManager.getCurrentBpm()
        val stepDurationMs = 60000f / (bpm * 4)  // 16th note duration in ms
        val calculatedStep = (elapsedMs / stepDurationMs).toInt()

        return calculatedStep % 16
    }

    /**
     * Clean up MIDI resources.
     *
     * Call this in onDestroy() or when the app exits.
     */
    fun releaseMidi() {
        try {
            midiOutputPort?.close()
            midiDevice?.close()
            Log.i(TAG, "USB MIDI listener released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MIDI listener: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "ExternalMidiListener"
    }
}

/**
 * MidiReceiverImpl
 *
 * Bridges Android's MidiReceiver callback interface to our ExternalMidiListener logic.
 *
 * This is the "glue" between Android's MIDI system and our event handling.
 */
class MidiReceiverImpl(
    private val listener: ExternalMidiListener
) : MidiReceiver() {

    /**
     * Called by Android MIDI system when data is received on the port.
     *
     * Forwards to listener.onMidiReceived() for actual processing.
     */
    override fun onSend(data: ByteArray?, offset: Int, count: Int, timestamp: Long) {
        listener.onMidiReceived(data, offset, count, timestamp)
    }
}