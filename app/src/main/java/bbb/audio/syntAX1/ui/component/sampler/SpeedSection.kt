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
fun SpeedSection(
    speedRatio: Float,
    onSpeedRatioChange: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Speed: ${String.format("%.1f", speedRatio)}",
            style = MaterialTheme.typography.labelMedium
        )
        Slider(
            value = speedRatio,
            onValueChange = onSpeedRatioChange,
            valueRange = -60f..60f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}