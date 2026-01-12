package bbb.audio.syntAX1.ui.component.sequencer

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.animation.InteractiveKnob
import bbb.audio.syntAX1.ui.component.sequencer.blank.DropdownArrow
import bbb.audio.syntAX1.ui.component.sequencer.blank.StepButtonBlue

@Composable
fun SEQStepColumn(
    modifier: Modifier = Modifier,
    isOn: Boolean,
    isCurrentlyPlaying: Boolean,
    onToggle: () -> Unit,
    isFreeParameterVisible: Boolean,
    freeKnobValue: Float,
    onFreeKnobChange: (Float) -> Unit,
    veloKnobValue: Float,
    onVeloKnobChange: (Float) -> Unit,
    notePitch: Int,
    onNotePitchChange: (Int) -> Unit,
    stepLength: Long,
    onStepLengthChange: (Long) -> Unit,
    bpm: Float = 120f
) {
    var showNotePicker by remember { mutableStateOf(false) }
    var showLengthPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        AnimatedVisibility(visible = isFreeParameterVisible) {
            InteractiveKnob(
                modifier = Modifier.size(56.dp),
                value = freeKnobValue,
                onValueChange = onFreeKnobChange
            )
        }

        // === LENGTH PICKER DROPDOWN ===
        DropdownArrow(
            modifier = Modifier.clickable {
                Log.d("SEQStepColumn", "⏱️ Length dropdown clicked - opening picker")
                showLengthPicker = true
            }
        )

        InteractiveKnob(
            modifier = Modifier.size(56.dp),
            value = veloKnobValue,
            onValueChange = onVeloKnobChange
        )

        // === NOTE PICKER DROPDOWN ===
        DropdownArrow(
            modifier = Modifier.clickable {
                Log.d("SEQStepColumn", "🎵 Note dropdown clicked - opening picker")
                showNotePicker = true
            }
        )

        StepButtonBlue(
            isOn = isOn,
            isCurrentlyPlaying = isCurrentlyPlaying,
            onToggle = onToggle
        )
    }

    // ========== DIALOGS ==========
    // These must be rendered outside the Column!

    // Note Picker Dialog
    if (showNotePicker) {
        Log.d("SEQStepColumn", "Rendering NotePickerDialog")
        NotePickerDialog(
            onDismiss = {
                Log.d("SEQStepColumn", "Note picker dismissed")
                showNotePicker = false
            },
            onNoteSelected = { selectedNote ->
                Log.d("SEQStepColumn", "Note selected: $selectedNote")
                onNotePitchChange(selectedNote)
                showNotePicker = false
            },
            currentNote = notePitch
        )
    }

    // Length Picker Dialog
    if (showLengthPicker) {
        Log.d("SEQStepColumn", "Rendering LengthPickerDialog")
        LengthPickerDialog(
            onDismiss = {
                Log.d("SEQStepColumn", "Length picker dismissed")
                showLengthPicker = false
            },
            onLengthSelected = { selectedLength ->
                Log.d("SEQStepColumn", "Length selected: $selectedLength")
                onStepLengthChange(selectedLength)
                showLengthPicker = false
            },
            currentLength = stepLength,
            bpm = bpm
        )
    }
}