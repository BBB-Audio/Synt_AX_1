package bbb.audio.syntAX1.ui.component.sequencer.blank

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
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.component.sequencer.SEQStepColumn
import bbb.audio.syntAX1.ui.viewmodel.SequencerStepState

/**
 * This component now represents the permanent part of a sequencer row,
 * without the collapsible "Free" parameter lane.
 */
@Composable
fun SequencerRow(
    modifier: Modifier = Modifier,
    steps: List<SequencerStepState>,
    playingStepIndex: Int?,
    isFreeLaneVisible: Boolean,
    onStepToggled: (Int) -> Unit,
    onFreeKnobChanged: (Int, Float) -> Unit,
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
                        isFreeParameterVisible = isFreeLaneVisible,
                        freeKnobValue = step.freeKnobValue,
                        onFreeKnobChange = { newValue -> onFreeKnobChanged(step.id, newValue) },
                        veloKnobValue = step.veloKnobValue,
                        onVeloKnobChange = { newValue -> onVeloKnobChanged(step.id, newValue) },
                        notePitch = step.notePitch.toInt(),
                        onNotePitchChange = { note -> onNotePitchChanged(step.id, note) },
                        stepLength = step.stepLength,
                        onStepLengthChange = { length -> onStepLengthChanged(step.id, length) }
                    )
                }

            }
    }
}
