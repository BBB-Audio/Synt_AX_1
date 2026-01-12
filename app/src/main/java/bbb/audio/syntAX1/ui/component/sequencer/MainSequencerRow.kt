package bbb.audio.syntAX1.ui.component.sequencer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.viewmodel.SequencerStepState

@Composable
fun MainSequencerRow(
    modifier: Modifier = Modifier,
    steps: List<SequencerStepState>,
    playingStepIndex: Int?,
    bpm: Float = 120f,
    onStepToggled: (Int) -> Unit,
    onVeloKnobChanged: (Int, Float) -> Unit,
    onNotePitchChanged: (Int, Int) -> Unit,
    onStepLengthChanged: (Int, Long) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.tertiary,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEach { step ->
                SEQStepColumn(
                    modifier = Modifier.weight(1f),
                    isOn = step.isOn,
                    isCurrentlyPlaying = (step.id == playingStepIndex),
                    onToggle = { onStepToggled(step.id) },
                    isFreeParameterVisible = false,
                    freeKnobValue = step.freeKnobValue,
                    onFreeKnobChange = {},
                    veloKnobValue = step.veloKnobValue,
                    onVeloKnobChange = { newValue -> onVeloKnobChanged(step.id, newValue) },
                    notePitch = step.notePitch.toInt(),
                    onNotePitchChange = { note -> onNotePitchChanged(step.id, note) },
                    stepLength = step.stepLength,
                    onStepLengthChange = { length -> onStepLengthChanged(step.id, length) },
                    bpm = bpm
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainSequencerRowPreview() {
    val previewSteps = List(4) { SequencerStepState(id = it) }
    MainSequencerRow(
        steps = previewSteps,
        playingStepIndex = 1,
        bpm = 120f,
        onStepToggled = {},
        onVeloKnobChanged = { _, _ -> },
        onNotePitchChanged = { _, _ -> },
        onStepLengthChanged = { _, _ -> }
    )
}