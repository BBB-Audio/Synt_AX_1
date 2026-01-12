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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.R

@Composable
fun StepButton(
    isOn: Boolean,
    isCurrentlyPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawableId = when {
        isCurrentlyPlaying && isOn -> R.drawable.on_glow_frame
        isCurrentlyPlaying && !isOn -> R.drawable.off_glow_frame
        isOn -> R.drawable.on_frame
        else -> R.drawable.off_frame
    }

    // The Box defines the layout space and the clickable area.
    // The Image is drawn larger inside and centered, allowing the glow to "bleed" out.
    Box(
        modifier = modifier
            .size(48.dp) // Defines the space the button takes in the parent layout.
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Disable ripple effect
                onClick = onToggle
            ),
        contentAlignment = Alignment.Center // Center the large image within the smaller Box
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "Step button",
            // The Image is visually larger than the Box.
            modifier = Modifier.size(width = 195.dp, height = 200.dp),
            contentScale = ContentScale.None
        )
    }
}

//region Previews
@Preview(name = "Off")
@Composable
private fun StepButtPreview() {
    StepButton(isOn = false, isCurrentlyPlaying = false, onToggle = {})
}
@Preview(name = "On")
@Composable
private fun StepButtPreviewOn() {
    StepButton(isOn = true, isCurrentlyPlaying = false, onToggle = {})
}
@Preview(name = "Off + Playing")
@Composable
private fun StepButtPreviewPlay() {
    StepButton(isOn = false, isCurrentlyPlaying = true, onToggle = {})
}
@Preview(name = "On + Playing")
@Composable
private fun StepButtPreviewPlayOn() {
    StepButton(isOn = true, isCurrentlyPlaying = true, onToggle = {})
}
//endregion