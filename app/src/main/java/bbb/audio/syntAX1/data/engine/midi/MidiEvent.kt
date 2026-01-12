package bbb.audio.syntAX1.data.engine.midi

import android.util.Log
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Core MIDI Event representation
 * Agnostic to source (internal sequencer, external hardware, etc.)
 */
data class MidiEvent(
    val noteNumber: Int,              // 0-127 (60 = Middle C)
    val velocity: Int,                // 0-127 (0 = Note Off, >0 = Note On)
    val stepIndex: Int,               // Which 16th step (0-15)
    val timestamp: Long = System.currentTimeMillis(),
    val type: MidiEventType = MidiEventType.NOTE_ON,
    val channel: Int = 0,             // MIDI channel (0-15)
    val source: MidiSource = MidiSource.INTERNAL
) {
    /**
     * Convenience: Is this a Note-Off event?
     * (velocity == 0 OR explicit NOTE_OFF type)
     */
    fun isNoteOff(): Boolean =
        velocity == 0 || type == MidiEventType.NOTE_OFF

    /**
     * Convenience: Is this a Note-On event?
     */
    fun isNoteOn(): Boolean =
        velocity > 0 && type == MidiEventType.NOTE_ON
}

enum class MidiEventType {
    NOTE_ON,
    NOTE_OFF,
    CC,
    PITCH_BEND,
    PROGRAM_CHANGE
}

enum class MidiSource {
    INTERNAL,           // From Sequencer UI
    EXTERNAL_USB,       // From USB MIDI device
    EXTERNAL_BLE,       // From Bluetooth MIDI (future Feature from Synt_AX_2)
    SEQUENCER           // Alias for INTERNAL
}

/**
 * Thread-safe MIDI Event Pool
 * Acts as a buffer between multiple MIDI sources and the PD Clock
 *
 * Design Pattern: Producer-Consumer
 * - Producers: SequencerUI, ExternalMidiListener
 * - Consumer: PdClockEngine (via PdReceiverImpl.onClockTick)
 */
class MidiEventPool {

    private val eventQueue = ConcurrentLinkedQueue<MidiEvent>()
    private val lock = Any()

    /**
     * Add a MIDI event to the queue
     * Thread-safe, non-blocking
     */
    fun enqueue(event: MidiEvent) {
        eventQueue.offer(event)
        Log.d(TAG, "Enqueued: ${event.source} - Note ${event.noteNumber} (vel: ${event.velocity}) on step ${event.stepIndex}")
    }

    /**
     * Add multiple MIDI events at once
     */
    fun enqueueAll(events: List<MidiEvent>) {
        events.forEach { eventQueue.offer(it) }
        Log.d(TAG, "Enqueued ${events.size} events")
    }

    /**
     * Retrieve all events scheduled for a specific step
     * Removes them from the queue (consume semantics)
     * Thread-safe
     */
    fun getEventsForStep(stepIndex: Int): List<MidiEvent> {
        synchronized(lock) {
            val events = eventQueue
                .filter { it.stepIndex == stepIndex }
                .toList()

            // Remove consumed events
            eventQueue.removeAll(events)

            if (events.isNotEmpty()) {
                Log.d(TAG, "Got ${events.size} events for step $stepIndex")
            }

            return events
        }
    }

    /**
     * Peek at all pending events without consuming them
     * For debugging/UI display
     */
    fun peekAll(): List<MidiEvent> {
        return eventQueue.toList()
    }

    /**
     * Get all events for a specific MIDI note number (regardless of step)
     * For finding pending Note-Offs for a given note
     */
    fun getEventsForNote(noteNumber: Int): List<MidiEvent> {
        synchronized(lock) {
            val events = eventQueue
                .filter { it.noteNumber == noteNumber }
                .toList()
            eventQueue.removeAll(events)
            return events
        }
    }

    /**
     * Clear the entire queue
     * For stopping/resetting sequencer
     */
    fun clear() {
        synchronized(lock) {
            eventQueue.clear()
            Log.d(TAG, "Event pool cleared")
        }
    }

    /**
     * Get queue size for monitoring
     */
    fun size(): Int = eventQueue.size

    /**
     * Get events for a range of steps (e.g., lookahead for UI rendering)
     */
    fun getEventsForStepRange(fromStep: Int, toStep: Int): List<MidiEvent> {
        synchronized(lock) {
            val events = eventQueue
                .filter { it.stepIndex in fromStep..toStep }
                .toList()
            return events  // Don't consume - just peek
        }
    }

    companion object {
        private const val TAG = "MidiEventPool"
    }
}