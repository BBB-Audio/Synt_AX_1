package bbb.audio.syntAX1.ui.component.sampler

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import bbb.audio.syntAX1.ui.viewmodel.SampleScrubberState

@Composable
fun ScrubSection(
    modifier: Modifier = Modifier,
    sampleScrubberState: SampleScrubberState,
    onChunkSizeChange: (Float) -> Unit,
    onReadPointChange: (Float) -> Unit
) {
    val chunkSize = 0f..1000f
    val readPoint = -250f..1000f

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
                text = "Scrub",
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
                Spacer(modifier = Modifier.weight(0.25f))
                // Chunk Size Knob
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        "Chunk Size: ${sampleScrubberState.chunkSize.toInt()} ms",
                        style = MaterialTheme.typography.labelMedium
                    )
                    InteractiveKnob(
                        modifier = Modifier.size(90.dp),
                        value = (sampleScrubberState.chunkSize - chunkSize.start) / (chunkSize.endInclusive - chunkSize.start),
                        onValueChange = { knobValue ->
                            val newSize = (knobValue * (chunkSize.endInclusive - chunkSize.start)) + chunkSize.start
                            onChunkSizeChange(newSize)
                        }
                    )
                }
Spacer(modifier = Modifier.weight(1f))
                // Playback Speed Knob
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Read Point: ${sampleScrubberState.readPoint.toInt()} ms",
                        style = MaterialTheme.typography.labelMedium)
                    InteractiveKnob(
                        modifier = Modifier.size(90.dp),
                        value = (sampleScrubberState.readPoint - readPoint.start) / (readPoint.endInclusive - readPoint.start),
                        onValueChange = { knobValue ->
                            val newPoint = (knobValue * (readPoint.endInclusive - readPoint.start)) + readPoint.start
                            onReadPointChange(newPoint)
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(0.25f))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ScrubSectionPreview() {
    Synt_AX_1Theme {
        ScrubSection(
            sampleScrubberState = SampleScrubberState(chunkSize = 6.0f, readPoint = 1.5f),
            onChunkSizeChange = {},
            onReadPointChange = {}
        )
    }
}