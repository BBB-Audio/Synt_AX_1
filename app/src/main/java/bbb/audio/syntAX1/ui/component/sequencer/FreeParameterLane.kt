package bbb.audio.syntAX1.ui.component.sequencer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.animation.InteractiveKnob
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme
import bbb.audio.syntAX1.ui.viewmodel.SequencerStepState

@Composable
fun FreeParameterLane(
    modifier: Modifier = Modifier,
    steps: List<SequencerStepState>,
    label: String, // The Dynamic label
    onFreeKnobChanged: (Int, Float) -> Unit
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEach { step ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        InteractiveKnob(
                            modifier = Modifier.size(56.dp),
                            value = step.freeKnobValue,
                            onValueChange = { newValue -> onFreeKnobChanged(step.id, newValue) }
                        )
                    }
                }
            }
        }
        // The centered label for this lane
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Color.Black.copy(alpha = 0.7f))
    }
}

@Preview
@Composable
private fun FreeParameterLanePreview() {
    val previewSteps = List(4) { SequencerStepState(id = it) }
    Synt_AX_1Theme() {
    FreeParameterLane(
        steps = previewSteps,
        label = "Cutoff", // Example label
        onFreeKnobChanged = { _, _ -> }
    )
    }
}
