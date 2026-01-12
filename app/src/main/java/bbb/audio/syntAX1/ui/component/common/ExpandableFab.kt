package bbb.audio.syntAX1.ui.component.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.R
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun ExpandableFab(
    modifier: Modifier = Modifier,
    fabAlignment: Alignment.Horizontal,
    expanded: Boolean,
    onFabClick: () -> Unit,
    isFreeLaneVisible: Boolean,
    onFreeLaneClick: () -> Unit,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    onBpmClick: () -> Unit,
    onSavePatternClick: () -> Unit,
    onLoadPatternClick: () -> Unit
) {
    val fabAnimComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fab_main))
    val playStopComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fab_play_stop))
    val recordStopComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fab_rec_stop_2))
    val bpmComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bpm))

    val fabProgress by animateFloatAsState(
        targetValue = if (expanded) 0.5f else 0f,
        label = "FabProgress"
    )
    val playStopProgress by animateFloatAsState(
        targetValue = if (isPlaying) 0.5f else 0f,
        label = "PlayStopProgress"
    )
    val recordStopProgress by animateFloatAsState(
        targetValue = if (isRecording) 1f else 0f,
        label = "RecordStopProgress"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val mainFabColumn = @Composable {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Load Pattern Button
                        SmallFloatingActionButton(
                            onClick = onLoadPatternClick,
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.load),
                                contentDescription = "Load Pattern",
                                modifier = Modifier.scale(2.5f),
                                tint = Color.Unspecified
                            )
                        }
                        // Save Pattern Button
                        SmallFloatingActionButton(
                            onClick = onSavePatternClick,
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.save),
                                contentDescription = "Save Pattern",
                                modifier = Modifier.scale(2.5f),
                                tint = Color.Unspecified
                            )
                        }

                        SmallFloatingActionButton(onClick = onBpmClick, shape = CircleShape) {
                            LottieAnimation(
                                composition = bpmComposition,
                                isPlaying = true,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier
                                    .size(28.dp)
                                    .scale(1.2f)
                            )
                        }
                        SmallFloatingActionButton(onClick = onRecordClick, shape = CircleShape) {
                            LottieAnimation(
                                composition = recordStopComposition,
                                progress = { recordStopProgress },
                                modifier = Modifier
                                    .size(32.dp)
                                    .scale(3.2f)
                            )
                        }
                        SmallFloatingActionButton(onClick = onPlayClick, shape = CircleShape) {
                            LottieAnimation(
                                composition = playStopComposition,
                                progress = { playStopProgress },
                                modifier = Modifier
                                    .size(32.dp)
                                    .scale(1.2f)
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = onFabClick,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp),
                ) {
                    LottieAnimation(
                        composition = fabAnimComposition,
                        progress = { fabProgress },
                        modifier = Modifier.size(200.dp).scale(2.5f)
                    )
                }
            }
        }

        val sideButton = @Composable {
            AnimatedVisibility(visible = expanded) {
                SmallFloatingActionButton(onClick = onFreeLaneClick, shape = CircleShape) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Toggle Free Lane")
                }
            }
        }

        if (fabAlignment == Alignment.End) {
            sideButton()
            mainFabColumn()
        } else {
            mainFabColumn()
            sideButton()
        }
    }
}

//region Preview
@Preview(showBackground = true, name = "FAB on Right")
@Composable
private fun ExpandableFabPreviewRight() {
    var expanded by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var isFreeLaneVisible by remember { mutableStateOf(true) }

    Synt_AX_1Theme {
        ExpandableFab(
            fabAlignment = Alignment.End,
            expanded = expanded,
            onFabClick = { expanded = !expanded },
            isFreeLaneVisible = isFreeLaneVisible,
            onFreeLaneClick = { isFreeLaneVisible = !isFreeLaneVisible },
            isPlaying = isPlaying,
            onPlayClick = { isPlaying = !isPlaying },
            isRecording = isRecording,
            onRecordClick = { isRecording = !isRecording },
            onBpmClick = {},
            onSavePatternClick = { },
            onLoadPatternClick = { }
        )
    }
}

@Preview(showBackground = true, name = "FAB on Left")
@Composable
private fun ExpandableFabPreviewLeft() {
    var expanded by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var isFreeLaneVisible by remember { mutableStateOf(true) }

    Synt_AX_1Theme {
        ExpandableFab(
            fabAlignment = Alignment.Start,
            expanded = expanded,
            onFabClick = { expanded = !expanded },
            isFreeLaneVisible = isFreeLaneVisible,
            onFreeLaneClick = { isFreeLaneVisible = !isFreeLaneVisible },
            isPlaying = isPlaying,
            onPlayClick = { isPlaying = !isPlaying },
            isRecording = isRecording,
            onRecordClick = { isRecording = !isRecording },
            onBpmClick = {},
            onSavePatternClick = {},
            onLoadPatternClick = {}
        )
    }
}
//endregion
