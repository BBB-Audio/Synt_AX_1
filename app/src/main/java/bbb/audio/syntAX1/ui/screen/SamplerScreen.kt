package bbb.audio.syntAX1.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.data.local.entity.Sample
import bbb.audio.syntAX1.data.model.FreesoundSound
import bbb.audio.syntAX1.domain.PlaySamplePreviewUseCase
import bbb.audio.syntAX1.ui.component.common.SaveSoundBottomSheet
import bbb.audio.syntAX1.ui.component.common.SingleChoiceSegmentedButton
import bbb.audio.syntAX1.ui.component.sampler.PitchPlaybackSpeedSection
import bbb.audio.syntAX1.ui.component.synth.EnvelopeSection
import bbb.audio.syntAX1.ui.component.synth.FilterSection
import bbb.audio.syntAX1.ui.dialog.FreesoundSearchDialog
import bbb.audio.syntAX1.ui.dialog.LocalSampleDialog
import bbb.audio.syntAX1.ui.viewmodel.FilterType
import bbb.audio.syntAX1.ui.viewmodel.FreesoundEvent
import bbb.audio.syntAX1.ui.viewmodel.FreesoundUiState
import bbb.audio.syntAX1.ui.viewmodel.FreesoundViewModel
import bbb.audio.syntAX1.ui.viewmodel.SamplerState
import bbb.audio.syntAX1.ui.viewmodel.SamplerViewModel

@Composable
fun SamplerScreen(
    modifier: Modifier = Modifier,
    freesoundViewModel: FreesoundViewModel,
    samplerViewModel: SamplerViewModel
) {
    val freesoundUiState by freesoundViewModel.uiState.collectAsState()
    val currentlyPlayingId by freesoundViewModel.currentlyPlayingId.collectAsState()
    val previewUrl by freesoundViewModel.previewUrl.collectAsState()
    val samplerState by samplerViewModel.uiState.collectAsState()
    val samples by samplerViewModel.samples.collectAsState()

    SamplerScreenContent(
        modifier = modifier,
        freesoundUiState = freesoundUiState,
        currentlyPlayingId = currentlyPlayingId,
        previewUrl = previewUrl,
        samplerState = samplerState,
        samples = samples,
        // Pass the whole ViewModel down to allow direct function calls
        freesoundViewModel = freesoundViewModel,
        onLocalSampleSelected = {
            samplerViewModel.loadSampleFromFile(it)
        },
        onPitchChange = samplerViewModel::onPitchChange,
        onPlaybackSpeedChange = samplerViewModel::onPlaybackSpeedChange,
        onAttackChange = samplerViewModel::onAttackChange,
        onDecayChange = samplerViewModel::onDecayChange,
        onSustainChange = samplerViewModel::onSustainChange,
        onReleaseChange = samplerViewModel::onReleaseChange,
        onCutoffChange = samplerViewModel::onCutoffChange,
        onResonanceChange = samplerViewModel::onResonanceChange,
        onFilterTypeChange = samplerViewModel::onFilterTypeChange,
        onTriggerSample = samplerViewModel::triggerSample
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SamplerScreenContent(
    modifier: Modifier = Modifier,
    freesoundUiState: FreesoundUiState,
    currentlyPlayingId: Int?,
    previewUrl: String?,
    samplerState: SamplerState,
    samples: List<Sample>,
    // The ViewModel is passed to allow direct calls, e.g. for saving.
    freesoundViewModel: FreesoundViewModel,
    onLocalSampleSelected: (Sample) -> Unit,
    onPitchChange: (Float) -> Unit,
    onPlaybackSpeedChange: (Float) -> Unit,
    onAttackChange: (Float) -> Unit,
    onDecayChange: (Float) -> Unit,
    onSustainChange: (Float) -> Unit,
    onReleaseChange: (Float) -> Unit,
    onCutoffChange: (Float) -> Unit,
    onResonanceChange: (Float) -> Unit,
    onFilterTypeChange: (FilterType) -> Unit,
    onTriggerSample: () -> Unit
) {
    val context = LocalContext.current
    var showLoadDialog by remember { mutableStateOf(false) }
    var showSaveSheet by remember { mutableStateOf(false) }
    var selectedSoundForSaving by remember { mutableStateOf<FreesoundSound?>(null) }
    var selectedSearchOption by remember { mutableIntStateOf(0) }
    val searchOptions = listOf("Online", "Offline")
    val playPreviewUseCase = remember { PlaySamplePreviewUseCase(context) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                Text("SAMPLE OSC", style = MaterialTheme.typography.headlineSmall)
                SingleChoiceSegmentedButton(
                    options = searchOptions,
                    selectedIndex = selectedSearchOption,
                    onOptionSelect = { selectedSearchOption = it }
                )
                Button(onClick = { showLoadDialog = true }) {
                    Text("Load Sample", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        PitchPlaybackSpeedSection(
            oscState = samplerState.sOsc,
            onPitchChange = onPitchChange,
            onPlaybackSpeedChange = onPlaybackSpeedChange
        )

        EnvelopeSection(
            attack = samplerState.sEnvelope.sAttack,
            decay = samplerState.sEnvelope.sDecay,
            sustain = samplerState.sEnvelope.sSustain,
            release = samplerState.sEnvelope.sRelease,
            onAttackChange = onAttackChange,
            onDecayChange = onDecayChange,
            onSustainChange = onSustainChange,
            onReleaseChange = onReleaseChange
        )

        FilterSection(
            modifier = Modifier.fillMaxWidth(),
            cutoff = samplerState.sFilter.sCutoff,
            resonance = samplerState.sFilter.sResonance,
            type = samplerState.sFilter.sType,
            onCutoffChange = onCutoffChange,
            onResonanceChange = onResonanceChange,
            onTypeChange = onFilterTypeChange
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onTriggerSample,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(
                "TRIGGER (C4)",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

    if (showLoadDialog) {
        when (selectedSearchOption) {
            0 -> FreesoundSearchDialog(
                uiState = freesoundUiState,
                currentlyPlayingId = currentlyPlayingId,
                previewUrl = previewUrl,
                onEvent = { event ->
                    when (event) {
                        is FreesoundEvent.SelectSound -> {
                            selectedSoundForSaving = event.sound
                            showLoadDialog = false
                            showSaveSheet = true
                        }
                        FreesoundEvent.Dismiss -> showLoadDialog = false
                        else -> freesoundViewModel.onEvent(event)
                    }
                }
            )
            1 -> LocalSampleDialog(
                samples = samples,
                playSamplePreviewUseCase = playPreviewUseCase,
                onDismiss = { showLoadDialog = false },
                onLocalSampleSelected = {
                    onLocalSampleSelected(it)
                    showLoadDialog = false
                }
            )
        }
    }

    if (showSaveSheet && selectedSoundForSaving != null) {
        SaveSoundBottomSheet(
            initialName = selectedSoundForSaving!!.name,
            onSave = { customName ->
                // CORRECTED: Direct call to the public function on the ViewModel.
                // This fixes the 'Unresolved reference' error.
                freesoundViewModel.saveSoundToLibrary(selectedSoundForSaving!!.id, customName)
                showSaveSheet = false
                selectedSoundForSaving = null
            },
            onDismiss = {
                showSaveSheet = false
                selectedSoundForSaving = null
            }
        )
    }
}
