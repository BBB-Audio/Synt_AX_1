package bbb.audio.syntAX1.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.viewmodel.SettingsViewModel
import org.koin.compose.koinInject

@Composable
fun SettingsContent(onDismiss: () -> Unit) {
    val settingsViewModel: SettingsViewModel = koinInject()
    val state by settingsViewModel.uiState.collectAsState()
    val sampleRates = listOf(22050, 44100, 48000)
    val themes = listOf("Default", "Girlie")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // This makes the settings area scrollable if it exceeds the screen height
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // --- Theme Selection ---
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "App Theme",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        themes.forEachIndexed { index, themeName ->
                            SegmentedButton(
                                selected = state.themeName == themeName,
                                onClick = { settingsViewModel.updateTheme(themeName) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = themes.size
                                )
                            ) {
                                Text(themeName)
                            }
                        }
                    }
                }
            }

            // --- Sample Rate Selection ---
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sample Rate",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        sampleRates.forEachIndexed { index, rate ->
                            SegmentedButton(
                                selected = state.sampleRate == rate,
                                onClick = { settingsViewModel.setSampleRate(rate) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = sampleRates.size
                                )
                            ) {
                                Text("$rate Hz")
                            }
                        }
                    }
                }
            }

            // --- Output Volume ---
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Output Volume: ${(state.outputVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Slider(
                        value = state.outputVolume,
                        onValueChange = { settingsViewModel.setOutputVolume(it) },
                        valueRange = 0f..1f,
                        steps = 99,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // --- MIDI SETTINGS (RESTORED) ---
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MIDI Settings",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Quantize MIDI Input", modifier = Modifier.weight(1f))
                        Switch(
                            checked = state.quantizeMidi,
                            onCheckedChange = { settingsViewModel.setQuantizeMidi(it) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enable USB MIDI", modifier = Modifier.weight(1f))
                        Switch(
                            checked = state.usbMidiEnabled,
                            onCheckedChange = { settingsViewModel.setUsbMidiEnabled(it) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Action Buttons ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    settingsViewModel.applySettings()
                    onDismiss() // Close the sheet after applying
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply & Close")
            }
            Button(
                onClick = settingsViewModel::resetToDefaults,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Reset to Defaults")
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Cancel")
            }
        }
    }
}