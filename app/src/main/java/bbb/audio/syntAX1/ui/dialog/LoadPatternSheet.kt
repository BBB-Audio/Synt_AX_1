package bbb.audio.syntAX1.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.data.local.entity.Pattern
import bbb.audio.syntAX1.ui.component.sequencer.PatternCarousel

/**
 * A bottom sheet that displays the PatternCarousel for loading a saved pattern.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadPatternSheet(
    patterns: List<Pattern>,
    onDismissRequest: () -> Unit,
    onPatternSelected: (Pattern) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        // This Column is just a container for the Carousel inside the sheet.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            PatternCarousel(
                patterns = patterns,
                onPatternSelected = onPatternSelected
            )
        }
    }
}
