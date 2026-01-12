package bbb.audio.syntAX1.ui.component.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bbb.audio.syntAX1.R
import bbb.audio.syntAX1.data.local.entity.Sample
import bbb.audio.syntAX1.ui.theme.grapeNutsFont

/**
 * Composable that displays a sample as a floppy disk card.
 * Includes play and delete functionality.
 */
@Composable
fun SampleFloppyCard(
    sample: Sample,
    onPreviewClick: (Sample) -> Unit = {},
    onDeleteClick: (Sample) -> Unit = {}
) {
    Column(
        modifier = Modifier.height(330.dp)
    ){
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(R.drawable.disk_graphic),
                contentDescription = "Sample: ${sample.name}",
                modifier = Modifier.height(300.dp)
            )
            Column(
                modifier = Modifier
                    .padding(bottom = 20.dp, start = 50.dp)
                    .align(Alignment.Center)
                    .offset(y = 30.dp)
            ) {
                Text(
                    text = "Sample Name:",
                    fontFamily = grapeNutsFont,
                    fontSize = 24.sp,
                    color = Color.Black
                )
                Text(
                    text = sample.name,
                    style = TextStyle(
                        fontFamily = grapeNutsFont,
                        fontSize = 30.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .width(180.dp)
                        .padding(start = 8.dp)
                )
            }
        }
        // Play and Delete buttons in a row
        Row(
            modifier = Modifier

                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Play button
            IconButton(
                onClick = { onPreviewClick(sample) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Preview Sound",
                    tint = Color.Green
                )
            }
            Spacer(modifier = Modifier.width(195.dp))
            // Delete button
            IconButton(
                onClick = { onDeleteClick(sample) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Sample",
                    tint = Color.Red
                )
            }
        }
    }

}

@Preview
@Composable
private fun SFCPreview() {
    SampleFloppyCard(
        sample = Sample(id = 1, name = "Kick Drum", duration = 1.5f),

    )
}