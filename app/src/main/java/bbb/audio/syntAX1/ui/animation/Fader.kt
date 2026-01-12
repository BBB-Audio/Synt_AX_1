package bbb.audio.syntAX1.ui.animation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * A simple, looping loading animation using the original 3-fader json.
 *
 * @param modifier The modifier to be applied to the animation.
 */
@Composable
fun FaderLoadingAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fader_animation))

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever, // Loop forever
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun FaderLoadingPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FaderLoadingAnimation(
            modifier = Modifier.size(100.dp, 250.dp)
        )
    }
}
