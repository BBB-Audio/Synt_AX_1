package bbb.audio.syntAX1.ui.component.sequencer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme

@Composable
fun NotePickerDialog(
    onDismiss: () -> Unit,
    onNoteSelected: (Int) -> Unit,
    currentNote: Int
) {
    // --- DATA PREPARATION ---
    val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val minNote = 36  // C2
    val maxNote = 95  // B7
    val notesPerColumn = 12 // One octave per column

    // The logic to sort the notes for a vertical piano layout
    val notesForGrid = remember {
        (minNote..maxNote).sortedWith(
            compareBy(
                { it / notesPerColumn }, // Sort by octave/column first
                { -(it % notesPerColumn) } // Then by note within the octave, descending
            )
        )
    }

    val blackKeyIndices = setOf(1, 3, 6, 8, 10)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Note") },
        text = {
            // --- UI LAYOUT ---
            LazyHorizontalGrid(
                rows = GridCells.Fixed(notesPerColumn), // Fixed number of rows defines the height of a column
                modifier = Modifier
                    .height(450.dp) // A fixed height for the grid is required
                    .width(320.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items = notesForGrid, key = { it }) { noteNumber ->
                    val octave = (noteNumber / 12) - 1
                    val noteName = noteNames[noteNumber % 12]
                    val displayText = "$noteName$octave"
                    val isSelected = noteNumber == currentNote

                    // Color logic
                    val isBlackKey = blackKeyIndices.contains(noteNumber % 12)
                    val backgroundColor = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isBlackKey -> MaterialTheme.colorScheme.surface
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                    val textColor = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        isBlackKey -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(48.dp)
                            .background(
                                color = backgroundColor,
                                shape = MaterialTheme.shapes.small
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable {
                                onNoteSelected(noteNumber)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = textColor,
                            fontSize = 12.sp
                        )
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
private fun NpdPreview() {
    Synt_AX_1Theme {
    NotePickerDialog(
        onDismiss = {},
        onNoteSelected = {},
        currentNote = 60
    )
    }
}