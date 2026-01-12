package bbb.audio.syntAX1.ui.component.synth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.animation.InteractiveKnob
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme

@Composable
fun EnvelopeSection(
    modifier: Modifier = Modifier,
    attack: Float,
    decay: Float,
    sustain: Float,
    release: Float,
    onAttackChange: (Float) -> Unit,
    onDecayChange: (Float) -> Unit,
    onSustainChange: (Float) -> Unit,
    onReleaseChange: (Float) -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Envelope",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attack Knob
                KnobColumn(
                    label = "Attack",
                    value = attack,
                    onValueChange = onAttackChange
                )
                // Decay Knob
                KnobColumn(
                    label = "Decay",
                    value = decay,
                    onValueChange = onDecayChange
                )
                // Sustain Knob
                KnobColumn(
                    label = "Sustain",
                    value = sustain,
                    onValueChange = onSustainChange
                )
                // Release Knob
                KnobColumn(
                    label = "Release",
                    value = release,
                    onValueChange = onReleaseChange
                )
            }
        }
    }
}

@Composable
private fun KnobColumn(
    modifier: Modifier = Modifier,
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        InteractiveKnob(
            modifier = Modifier.size(50.dp),
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EnvelopeSectionPreview() {
    Synt_AX_1Theme {
        EnvelopeSection(
            attack = 0.1f,
            decay = 0.2f,
            sustain = 0.7f,
            release = 0.3f,
            onAttackChange = {},
            onDecayChange = {},
            onSustainChange = {},
            onReleaseChange = {}
        )
    }
}