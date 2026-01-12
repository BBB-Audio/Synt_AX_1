package bbb.audio.syntAX1.ui.animation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bbb.audio.syntAX1.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * A Lottie-based button that plays a one-shot animation on click.
 *
 * @param modifier The modifier to be applied to the button.
 * @param onClick The callback to be invoked when the button is clicked. The animation starts automatically.
 */
@Composable
fun RecordButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.voice))
    var isPlaying by remember { mutableStateOf(false) }

    // This state will animate the progress from 0f to 1f when isPlaying is true.
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        restartOnPlay = true, // Ensures it replays every time
        speed = 1.5f
    )

    // When the animation finishes (progress is 1.0f), reset the isPlaying state.
    if (progress == 1f) {
        isPlaying = false
    }

    Box(
        modifier = modifier
            .aspectRatio(1f) // Ensure the button is square
            .clickable(
                // Disable clicking while the animation is running to prevent re-triggering
                enabled = !isPlaying,
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // No ripple effect
                onClick = {
                    // Trigger the user's onClick and start our animation
                    onClick()
                    isPlaying = true
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            // The animation is now driven by this progress value
            progress = { progress }
        )
    }
}
