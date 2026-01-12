package bbb.audio.syntAX1.ui.component.sampler

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.data.model.FreesoundSound

@Composable
fun SoundResultItem(
    sound: FreesoundSound,
    isPreviewPlaying: Boolean,
    onSelect: () -> Unit,
    onPreviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sound.name, style = MaterialTheme.typography.bodyLarge)
                Text("by ${sound.username}", style = MaterialTheme.typography.bodySmall)
            }

            IconButton(
                onClick = onPreviewClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isPreviewPlaying) Icons.Filled.Clear else Icons.Filled.PlayArrow,
                    contentDescription = "Preview",
                    tint = if (isPreviewPlaying) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}