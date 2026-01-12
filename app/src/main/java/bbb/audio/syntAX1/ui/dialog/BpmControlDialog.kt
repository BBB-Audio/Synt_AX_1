package bbb.audio.syntAX1.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import bbb.audio.syntAX1.ui.animation.InteractiveKnob
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun BpmControlDialog(
    currentBpm: Float,
    onBpmChanged: (Float) -> Unit,
    onDismissRequest: () -> Unit
) {
    val bpmFormatter = remember { DecimalFormat("#.0") }
    var isEditing by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }
    
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val commitChange = {
        textFieldValue.toFloatOrNull()?.let {
            onBpmChanged(it)
        }
        isEditing = false
        keyboardController?.hide()
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isEditing) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        modifier = Modifier.focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { commitChange() }),
                        singleLine = true
                    )
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                } else {
                    Text(
                        text = "BPM: ${bpmFormatter.format(currentBpm)}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable {
                            textFieldValue = currentBpm.roundToInt().toString()
                            isEditing = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                InteractiveKnob(
                    modifier = Modifier.size(120.dp),
                    value = ((currentBpm - 20f) / 280f).coerceIn(0f, 1f),
                    onValueChange = {
                        val newBpm = 20f + (it * 280f)
                        onBpmChanged(newBpm)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun BpmControlDialogPreview() {
    var bpm by remember { mutableFloatStateOf(120f) }
    Synt_AX_1Theme {
        Box(modifier = Modifier.fillMaxWidth()) {
            BpmControlDialog(
                currentBpm = bpm,
                onBpmChanged = { bpm = it },
                onDismissRequest = {}
            )
        }
    }
}
