package bbb.audio.syntAX1.ui.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * A reusable Lottie-based toggle switch.
 *
 * @param modifier The modifier to be applied to the switch.
 * @param isChecked The current state of the switch (on/off).
 * @param onClick The callback invoked when the switch is clicked.
 */
@Composable
fun ToggleSwitch(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onClick: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.toggleswitch))

    // Lokaler State für Animation
    var localIsChecked by remember { mutableStateOf(isChecked) }

    val progress by animateFloatAsState(
        targetValue = if (localIsChecked) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "ToggleProgress"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    localIsChecked = !localIsChecked  // ← Toggle lokal!
                    onClick()  // ← Dann ViewModel updaten
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
        )
    }
}