package bbb.audio.syntAX1.ui.animation

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * An interactive knob that is controlled by vertical drag gestures and hoisted state.
 *
 * @param modifier The modifier to be applied to the knob.
 * @param value The current progress of the knob, from 0.0f to 1.0f.
 * @param onValueChange A callback that is invoked when the user drags the knob.
 * @param sensitivity The sensitivity of the drag gesture. A larger value means less sensitivity.
 */
@Composable
fun InteractiveKnob(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    sensitivity: Float = 500f
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.knob_animation))

    // Use rememberUpdatedState to ensure the drag gesture always has the latest value.
    // This prevents the closure from capturing a stale value.
    val latestValue by rememberUpdatedState(value)

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val verticalDrag = change.previousPosition.y - change.position.y
                    // Calculate the new value based on the LATEST value, not the initial one.
                    val newValue = (latestValue + verticalDrag / sensitivity).coerceIn(0f, 1f)
                    onValueChange(newValue)
                    change.consume()
                }
            }
    ) {
        LottieAnimation(
            composition = composition,
            progress = { value },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun KnobPreview() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        InteractiveKnob(
            modifier = Modifier.size(150.dp),
            value = 0.5f,
            onValueChange = {}
        )
    }
}
