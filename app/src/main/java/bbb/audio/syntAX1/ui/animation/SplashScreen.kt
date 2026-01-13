package bbb.audio.syntAX1.ui.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.R
import bbb.audio.syntAX1.data.repository.SampleRepository
import bbb.audio.syntAX1.ui.theme.synthetic_synchronism
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // --- State Management for UI elements ---
   var showTopAnimation by remember { mutableStateOf(false) }
    var showMiddleAnimation by remember { mutableStateOf(false) }
    var showBottomAnimation by remember { mutableStateOf(false) }
    var currentText by remember { mutableStateOf("") }


    // --- Main Sequencing Logic ---
    LaunchedEffect(Unit) {
        // Stage 1: App Title
        showMiddleAnimation = true
        currentText = "Starting SYNTH_AX_0.8.2 Beta"
        delay(6700)

        // Stage 2: Initialize Pure Data
        showTopAnimation = true
        currentText = "Initializing Pure Data"
        delay(7500)

        // Stage 3: Warm up Synth
        showBottomAnimation = true
        currentText = "Warming up Curcuits"
        delay(6000)

        // Finish: Navigate to main app
        onTimeout()
    }

    // --- Background Data Loading ---
    val context = LocalContext.current
    val sampleRepository: SampleRepository = koinInject()
    LaunchedEffect(Unit) {
        launch {
            sampleRepository.initializeDefaultSamples(context)
        }
    }

    // --- UI Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // --- Top Animation ---
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val alpha by animateFloatAsState(
                targetValue = if (showTopAnimation) 1f else 0f,
                animationSpec = tween(500),
                label = "TopAlpha"
            )
            Box(modifier = Modifier.graphicsLayer { this.alpha = alpha }) {
                SequencerAnimation()
            }
        }

        // --- Middle Animation ---
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val alpha by animateFloatAsState(
                targetValue = if (showMiddleAnimation) 1f else 0f,
                animationSpec = tween(500),
                label = "MiddleAlpha"
            )
            Box(modifier = Modifier.graphicsLayer { this.alpha = alpha }) {
                AnimatedKnob(modifier = Modifier.size(150.dp))
            }
        }

        // --- Bottom Animation ---
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val alpha by animateFloatAsState(
                targetValue = if (showBottomAnimation) 1f else 0f,
                animationSpec = tween(500),
                label = "BottomAlpha"
            )
            Box(modifier = Modifier.graphicsLayer { this.alpha = alpha }) {
                FaderLoadingAnimation(modifier = Modifier.size(100.dp, 250.dp))
            }
        }

        // --- LCD Text Display (Fixed at the bottom) ---
        LcdDisplay(
            text = currentText,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun LcdDisplay(text: String, modifier: Modifier = Modifier) {
    val LcdGreen = Color(0xFF39FF14)

    Card(
        modifier = modifier,
        border = BorderStroke(2.dp, LcdGreen.copy(alpha = 0.7f)),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = text,
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith
                            fadeOut(animationSpec = tween(1000))
                },
                label = "LcdTextAnimation"
            ) { targetText ->
                Text(
                    text = targetText,
                    style = MaterialTheme.typography.displayMedium.copy(fontFamily = synthetic_synchronism),
                    color = LcdGreen,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(velocity = 300.dp)
                )
            }
        }
    }
}

@Composable
private fun SequencerAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "SequencerLightTransition")
    val activeStepFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SequencerActiveStep"
    )
    val activeStep = activeStepFloat.toInt().coerceAtMost(3)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy((-120).dp)
        ) {
            for (i in 0..3) {
                val imageRes = if (i == activeStep) R.drawable.on_frame else R.drawable.off_frame
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Sequencer step ${i + 1}",
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedKnob(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.knob_animation))
    val infiniteTransition = rememberInfiniteTransition(label = "KnobTransition")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "KnobProgress"
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}




@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(onTimeout = {})
}