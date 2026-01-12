package bbb.audio.syntAX1.ui.component.sampler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PitchSection(
    transposition: Float,
    onTranspositionChange: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Transposition: ${transposition.toInt()} cents",
            style = MaterialTheme.typography.labelMedium
        )
        Slider(
            value = transposition,
            onValueChange = onTranspositionChange,
            valueRange = -1200f..1200f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}