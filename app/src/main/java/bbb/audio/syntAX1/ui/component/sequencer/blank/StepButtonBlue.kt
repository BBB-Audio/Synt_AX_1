package bbb.audio.syntAX1.ui.component.sequencer.blank

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.R

@Composable
fun StepButtonBlue(
    isOn: Boolean,
    isCurrentlyPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawableId = when {
        isCurrentlyPlaying && isOn -> R.drawable.blue_butt_on_p
        isCurrentlyPlaying && !isOn -> R.drawable.blue_butt_off_p
        isOn -> R.drawable.blue_butt_on_np
        else -> R.drawable.blue_butt_off_np
    }

    // 1. The Box provides a fixed-size "stage" for the button.
    //    This size is large enough to contain the biggest state of the button.
    // 2. contentAlignment centers any smaller content automatically.
    Box(
        modifier = modifier
            .size(56.dp) // The consistent size for the layout
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            ),
        contentAlignment = Alignment.Center // This is the key!
    ) {
        // 3. The Image has no size modifier. It will be drawn at its intrinsic (natural) size.
        //    The Box will then center it.
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "Step button",
        )
    }
}

//region Previews
@Preview(name = "Off (Small)")
@Composable
private fun StepButtBluPreview() {
    StepButtonBlue(isOn = false, isCurrentlyPlaying = false, onToggle = {})
}
@Preview(name = "On (Large)")
@Composable
private fun StepButtBluPreviewOn() {
    StepButtonBlue(isOn = true, isCurrentlyPlaying = false, onToggle = {})
}
@Preview(name = "Off + Playing (Small)")
@Composable
private fun StepButtBluPreviewPlay() {
    StepButtonBlue(isOn = false, isCurrentlyPlaying = true, onToggle = {})
}
@Preview(name = "On + Playing (Large)")
@Composable
private fun StepButtBluPreviewPlayOn() {
    StepButtonBlue(isOn = true, isCurrentlyPlaying = true, onToggle = {})
}
//endregion
