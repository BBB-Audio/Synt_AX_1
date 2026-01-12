package bbb.audio.syntAX1.ui.component.synth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bbb.audio.syntAX1.ui.animation.InteractiveKnob
import bbb.audio.syntAX1.ui.component.common.SingleChoiceSegmentedButton
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme
import bbb.audio.syntAX1.ui.viewmodel.FilterType

@Composable
fun FilterSection(
    modifier: Modifier = Modifier,
    cutoff: Float,
    resonance: Float,
    type: FilterType,
    onCutoffChange: (Float) -> Unit,
    onResonanceChange: (Float) -> Unit,
    onTypeChange: (FilterType) -> Unit
) {

    val filterTypes = listOf(FilterType.LOW_PASS, FilterType.HIGH_PASS)
    val filterTypeOptions = listOf("LP", "HP")

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {


        Text(
            text = "Filter",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cutoff Knob
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Cutoff", style = MaterialTheme.typography.labelMedium)
                InteractiveKnob(
                    modifier = Modifier
                        .size(80.dp),
                    value = cutoff,
                    onValueChange = onCutoffChange
                )

            }

            // LP/HP Switch
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SingleChoiceSegmentedButton(
                    options = filterTypeOptions,
                    selectedIndex = filterTypes.indexOf(type),
                    onOptionSelect = { index
                        ->
                        onTypeChange(filterTypes[index])
                    },
                    modifier = Modifier
                        .height(26.dp)
                        .width(82.dp),
                    textStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                )
            }
            // Resonance Knob
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Resonance", style = MaterialTheme.typography.labelMedium)
                InteractiveKnob(
                    modifier = Modifier.size(80.dp),
                    value = resonance,
                    onValueChange = onResonanceChange
                )
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
private fun FilterSectionPreview() {
    Synt_AX_1Theme {
        FilterSection(
            cutoff = 0.5f,
            resonance = 0.2f,
            type = FilterType.LOW_PASS,
            onCutoffChange = {},
            onResonanceChange = {},
            onTypeChange = {}
        )
    }
}
