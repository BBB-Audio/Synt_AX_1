package bbb.audio.syntAX1.ui.component.sampler

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import bbb.audio.syntAX1.ui.viewmodel.SampleOscState

@Composable
fun PitchPlaybackSpeedSection(
    modifier: Modifier = Modifier,
    oscState: SampleOscState,
    onPitchChange: (Float) -> Unit,
    onPlaybackSpeedChange: (Float) -> Unit
) {
    val pitchRange = -12f..12f
    val speedRange = 0.5f..2.0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pitch & Speed",
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
                // Pitch Knob
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Pitch: ${String.format("%.1f", oscState.sPitch)}", style = MaterialTheme.typography.labelMedium)
                    InteractiveKnob(
                        modifier = Modifier.size(60.dp),
                        value = (oscState.sPitch - pitchRange.start) / (pitchRange.endInclusive - pitchRange.start),
                        onValueChange = { knobValue ->
                            val newPitch = (knobValue * (pitchRange.endInclusive - pitchRange.start)) + pitchRange.start
                            onPitchChange(newPitch)
                        }
                    )
                }

                // Playback Speed Knob
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Speed: ${String.format("%.1f", oscState.playbackSpeed)}x", style = MaterialTheme.typography.labelMedium)
                    InteractiveKnob(
                        modifier = Modifier.size(60.dp),
                        value = (oscState.playbackSpeed - speedRange.start) / (speedRange.endInclusive - speedRange.start),
                        onValueChange = { knobValue ->
                            val newSpeed = (knobValue * (speedRange.endInclusive - speedRange.start)) + speedRange.start
                            onPlaybackSpeedChange(newSpeed)
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PitchPlaybackSpeedSectionPreview() {
    Synt_AX_1Theme {
        PitchPlaybackSpeedSection(
            oscState = SampleOscState(sPitch = 6.0f, playbackSpeed = 1.5f),
            onPitchChange = {},
            onPlaybackSpeedChange = {}
        )
    }
}