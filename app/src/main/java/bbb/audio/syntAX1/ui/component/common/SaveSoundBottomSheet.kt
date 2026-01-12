package bbb.audio.syntAX1.ui.component.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Bottom sheet for saving a sound with a custom name.
 * 
 * @param initialName The default name suggested for the sound
 * @param onSave Callback invoked when user confirms save with the chosen name
 * @param onDismiss Callback invoked when user dismisses the bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveSoundBottomSheet(
    initialName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var saveName by remember { mutableStateOf(initialName) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SaveDisk(saveName = saveName, onNameChange = { saveName = it })
            
            Button(
                onClick = {
                    if (saveName.isNotBlank()) {
                        onSave(saveName)
                        onDismiss()
                    }
                },
                modifier = Modifier.padding(top = 24.dp),
                enabled = saveName.isNotBlank()
            ) {
                Text("Save Sound")
            }
        }
    }
}