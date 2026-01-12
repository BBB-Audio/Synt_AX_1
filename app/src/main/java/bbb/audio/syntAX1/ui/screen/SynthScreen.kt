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

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.animation.InteractiveKnob
import bbb.audio.syntAX1.ui.component.synth.EnvelopeSection
import bbb.audio.syntAX1.ui.component.synth.FilterSection
import bbb.audio.syntAX1.ui.viewmodel.SequencerViewModel
import bbb.audio.syntAX1.ui.viewmodel.SynthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SynthScreen(
    modifier: Modifier = Modifier,
    synthViewModel: SynthViewModel,
    sequencerViewModel: SequencerViewModel
) {
    val synthState by synthViewModel.uiState.collectAsState()
    val sequencerState by sequencerViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val midiActivity = synthState.midiActivity

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

//region --- OSC Section ---
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Oscilators", style = MaterialTheme.typography.headlineSmall)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                Log.d("SynthScreen", "Prelisten Note-On triggered.")
                                synthViewModel.playNote(60, 100)
                                delay(500)
                                Log.d("SynthScreen", "Prelisten Note-Off triggered.")
                                synthViewModel.stopNote(60)
                            }
                        }
                    ) { // ### Prelisten Button ###
                        Text("Prelisten")
                    }

                    // --- Sub Volume Knob ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        InteractiveKnob(
                            modifier = Modifier.size(48.dp),
                            value = synthState.subVolume,
                            onValueChange = synthViewModel::onSubVolumeChange
                        )
                        Text("Sub", style = MaterialTheme.typography.labelMedium)
                    }

                    // --- Main Volume Knob ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        InteractiveKnob(
                            modifier = Modifier.size(60.dp),
                            value = synthState.mainVolume,
                            onValueChange = synthViewModel::onMainVolumeChange
                        )
                        Text("Volume", style = MaterialTheme.typography.labelMedium)
                    }

                    // LED's
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) { // ### MIDI In ###
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (midiActivity) Color.Green else Color.Gray)
                            )
                            Text("MIDI In")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) { // ### Beat Indicator ###
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (sequencerState.isBeatActive) Color.Yellow else Color.Gray)
                            )
                            Text("Beat")
                        }
                    }
                }
            }
        }
//endregion

//region --- Env & Filter ---
        EnvelopeSection(
            modifier = Modifier,
            attack = synthState.envelope.attack,
            decay = synthState.envelope.decay,
            sustain = synthState.envelope.sustain,
            release = synthState.envelope.release,
            onAttackChange = synthViewModel::onAttackChange,
            onDecayChange = synthViewModel::onDecayChange,
            onSustainChange = synthViewModel::onSustainChange,
            onReleaseChange = synthViewModel::onReleaseChange
        )

        FilterSection(
            modifier = Modifier.fillMaxWidth(),
            cutoff = synthState.filter.cutoff,
            resonance = synthState.filter.resonance,
            type = synthState.filter.type,
            onCutoffChange = synthViewModel::onCutoffChange,
            onResonanceChange = synthViewModel::onResonanceChange,
            onTypeChange = synthViewModel::onFilterTypeChange
        )
        //endregion

//region --- Chorus Section ---
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Chorus Depth
                    InteractiveKnob(
                        modifier = Modifier.size(52.dp),
                        value = synthState.chorus.depth,
                        onValueChange = synthViewModel::onChorusDepthChange
                    )
                    Text("Depth", style = MaterialTheme.typography.labelMedium)
                }

                Text("Chorus", style = MaterialTheme.typography.headlineSmall)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // LFO Speed
                    InteractiveKnob(
                        modifier = Modifier.size(52.dp),
                        value = synthState.chorus.lfoSpeed,
                        onValueChange = synthViewModel::onLfoSpeedChange
                    )
                    Text("Speed", style = MaterialTheme.typography.labelMedium)

                }
            }
        }
        //endregion

//region --- Delay Section ---
        ElevatedCard(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Amount
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    InteractiveKnob(
                        modifier = Modifier.size(52.dp),
                        value = synthState.delay.amount,
                        onValueChange = synthViewModel::onDelayAmountChange
                    )
                    Text("Amount", style = MaterialTheme.typography.labelMedium)
                }

                Text("Delay", style = MaterialTheme.typography.headlineSmall)

                // Time
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    InteractiveKnob(
                        modifier = Modifier.size(52.dp),
                        value = synthState.delay.time,
                        onValueChange = synthViewModel::onDelayTimeChange
                    )
                    Text("Time", style = MaterialTheme.typography.labelMedium)
                }

                // Feedback
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    InteractiveKnob(
                        modifier = Modifier.size(52.dp),
                        value = synthState.delay.feed,
                        onValueChange = synthViewModel::onDelayFeedChange
                    )
                    Text("Feedback", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
//endregion

//region --- Reverb Section ---
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Reverb", style = MaterialTheme.typography.headlineSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        InteractiveKnob(
                            modifier = Modifier.size(52.dp),
                            value = synthState.reverb.depth,
                            onValueChange = synthViewModel::onReverbDepthChange
                        )
                        Text("Depth", style = MaterialTheme.typography.labelMedium)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        InteractiveKnob(
                            modifier = Modifier.size(52.dp),
                            value = synthState.reverb.feed,
                            onValueChange = synthViewModel::onReverbFeedChange
                        )
                        Text("Liveness", style = MaterialTheme.typography.labelMedium)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        InteractiveKnob(
                            modifier = Modifier.size(52.dp),
                            value = synthState.reverb.crossfreq,
                            onValueChange = synthViewModel::onReverbCrossfreqChange
                        )
                        Text("X-Freq", style = MaterialTheme.typography.labelMedium)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        InteractiveKnob(
                            modifier = Modifier.size(52.dp),
                            value = synthState.reverb.damp,
                            onValueChange = synthViewModel::onReverbDampChange
                        )
                        Text("Damp", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
//endregion

    }
}