package bbb.audio.syntAX1.ui.screen

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.animation.RecordButton
import bbb.audio.syntAX1.ui.component.sampler.PitchSection
import bbb.audio.syntAX1.ui.component.sampler.ScrubSection
import bbb.audio.syntAX1.ui.component.sampler.SpeedSection
import bbb.audio.syntAX1.ui.component.sampler.VolumeSection
import bbb.audio.syntAX1.ui.viewmodel.SampleScrubberState
import bbb.audio.syntAX1.ui.viewmodel.SampleScrubberViewModel

@Composable
fun SampleScrubberScreen(
    modifier: Modifier = Modifier,
    sampleScrubberViewModel: SampleScrubberViewModel
) {
    val sampleScrubberState by sampleScrubberViewModel.uiState.collectAsState()

    SampleScrubberScreenContent(
        modifier = modifier,
        sampleScrubberState = sampleScrubberState,
        sampleScrubberViewModel = sampleScrubberViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SampleScrubberScreenContent(
    modifier: Modifier = Modifier,
    sampleScrubberState: SampleScrubberState,
    sampleScrubberViewModel: SampleScrubberViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Section (Record + Mute)
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Record",
                    style = MaterialTheme.typography.headlineSmall)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            if (sampleScrubberState.isMuted) "Muted" else "Unmuted",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (sampleScrubberState.isMuted) Color.Red else Color.Green
                        )

                        Switch(
                            checked = sampleScrubberState.isMuted,
                            onCheckedChange = {
                                Log.d("ScrubberScreen", "Mute toggled!")
                                sampleScrubberViewModel.toggleMute()
                            },
                            modifier = Modifier.scale(1.0f)
                        )

                        Text("Mute",
                            style = MaterialTheme.typography.labelMedium)
                    }

                    RecordButton(
                        modifier = Modifier
                            .size(120.dp)
                            .weight(1f)
                        ,
                        onClick = {
                            Log.d("ScrubberScreen", "Record Button clicked!")
                            sampleScrubberViewModel.triggerRecord()
                        }
                    )

                    Button(
                        onClick = {
                            Log.d("ScrubberScreen", "Reset clicked!")
                            sampleScrubberViewModel.reset()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                    ) {
                        Text("Reset")
                    }
                }

            }
        }

        // Scrub Section (Chunk Size + Read Point)
        ScrubSection(
            sampleScrubberState = sampleScrubberState,
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            onChunkSizeChange = sampleScrubberViewModel::onChunkSizeChange,
            onReadPointChange = sampleScrubberViewModel::onReadPointChange
        )
        // Pitch Section (Transposition)
        PitchSection(
            transposition = sampleScrubberState.transposition,
            onTranspositionChange = sampleScrubberViewModel::onTranspositionChange
        )

        // Speed Section (Speed Ratio)
        SpeedSection(
            speedRatio = sampleScrubberState.speedRatio,
            onSpeedRatioChange = sampleScrubberViewModel::onSpeedRatioChange
        )
        VolumeSection(
            volume = sampleScrubberState.volume,
            onVolumeChange = sampleScrubberViewModel::onVolumeChange)

//        Spacer(modifier = Modifier.weight(0f))
    }
}

// Preview can be re-enabled later if needed, but requires a valid ViewModel instance.
// For now, it's better to test on a real device.

