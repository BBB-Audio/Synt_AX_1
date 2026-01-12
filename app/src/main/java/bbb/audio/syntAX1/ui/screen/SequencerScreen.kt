/*
 * Copyright 2026 Dirk Hammacher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package bbb.audio.syntAX1.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.component.sequencer.FreeParameterLane
import bbb.audio.syntAX1.ui.component.sequencer.MainSequencerRow
import bbb.audio.syntAX1.ui.viewmodel.SequencerViewModel

@Composable
fun SequencerScreen(
    modifier: Modifier = Modifier,
    sequencerViewModel: SequencerViewModel
) {
    val uiState by sequencerViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
    ) {
        /** For each chunk of 4 steps, create a full row assembly
         *
         */
        items(uiState.steps.chunked(4)) { rowSteps ->
            Column {
                // The collapsible free parameter lane
                AnimatedVisibility(visible = uiState.isFreeLaneVisible) {
                    FreeParameterLane(
                        steps = rowSteps,
                        label = "Free", // Default label
                        onFreeKnobChanged = { stepId, newValue -> 
                            sequencerViewModel.onFreeKnobChanged(stepId, newValue) 
                        }
                    )
                }
                // The permanent main row
                MainSequencerRow(
                    steps = rowSteps,
                    playingStepIndex = uiState.playingStepIndex,
                    bpm = uiState.bpm,
                    onStepToggled = { stepId -> sequencerViewModel.onStepToggled(stepId) },
                    onVeloKnobChanged = { stepId, newValue -> sequencerViewModel.onVeloKnobChanged(stepId, newValue) },
                    onNotePitchChanged = { stepId, note -> sequencerViewModel.onNotePitchChanged(stepId, note) },
                    onStepLengthChanged = { stepId: Int, length: Long ->
                        sequencerViewModel.onStepLengthChanged(stepId, length)
                    }
                )
            }
        }
    }
}
