package bbb.audio.syntAX1.ui.component.sequencer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme
import bbb.audio.syntAX1.ui.theme.notesFont

@Composable
fun LengthPickerDialog(
    onDismiss: () -> Unit,
    onLengthSelected: (Long) -> Unit,
    currentLength: Long,
    bpm: Float
) {
    data class NoteLength(
        val label: String,
        val glyph: String,
        val durationMs: Long
    )
    // Calculate lengths based on BPM (in ms)
    // Assumption: 16th note = (60000 / bpm) / 4
    val stepDurationMs = (60000f / bpm) / 4

    val noteLengths = listOf(
        NoteLength("1/32", "32", (stepDurationMs * 0.5).toLong()), // 32tel
        NoteLength("1/16", "s", (stepDurationMs * 1).toLong()),   // 16tel
        NoteLength("1/8", "e", (stepDurationMs * 2).toLong()),    // 8tel
        NoteLength("1/4", "q", (stepDurationMs * 4).toLong()),    // Viertel
        NoteLength("1/2", "h", (stepDurationMs * 8).toLong()),    // Halbe
        NoteLength("1", "w", (stepDurationMs * 16).toLong()),   // Ganze
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Note Length") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(noteLengths.size) { index ->
                    val note = noteLengths[index]
                    val isSelected = note.durationMs == currentLength

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium
                            )
                            .clickable {
                                onLengthSelected(note.durationMs)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = note.glyph,
                                fontFamily = notesFont,
                                fontSize = 44.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
//                            Text(
//                                text = note.label,
//                                fontSize = 10.sp,
//                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
//                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
//                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
private fun LpdPreview() {
    Synt_AX_1Theme {
        LengthPickerDialog(
            onDismiss = {},
            onLengthSelected = {},
            currentLength = 500L,
            bpm = 120f
        )
    }
}