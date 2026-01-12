package bbb.audio.syntAX1.ui.component.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelect: (index: Int) -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onOptionSelect(index) },
                selected = index == selectedIndex,
                label = {
                    Text(
                        text = label,
                        style = textStyle
                    )
                }
            )
        }
    }
}
@Preview
@Composable
private fun SCSVPreview() {
    Synt_AX_1Theme {
        // A state to control the preview
        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = listOf("L", "M", "R")

        SingleChoiceSegmentedButton(
            options = options,
            selectedIndex = selectedIndex,
            onOptionSelect = { selectedIndex = it }
        )
    }
}
