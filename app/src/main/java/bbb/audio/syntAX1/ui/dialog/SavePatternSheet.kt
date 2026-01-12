package bbb.audio.syntAX1.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import bbb.audio.syntAX1.ui.component.common.SaveDisk

/**
 * A bottom sheet that uses the SaveDisk composable to get a name and save a pattern.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavePatternSheet(
    onDismissRequest: () -> Unit,
    onSaveClick: (patternName: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var patternName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SaveDisk(
                saveName = patternName,
                onNameChange = { patternName = it }
            )

            Button(
                onClick = {
                    if (patternName.isNotBlank()) {
                        onSaveClick(patternName)
                    }
                },
                enabled = patternName.isNotBlank()
            ) {
                Text("Save Pattern")
            }
        }
    }
}
