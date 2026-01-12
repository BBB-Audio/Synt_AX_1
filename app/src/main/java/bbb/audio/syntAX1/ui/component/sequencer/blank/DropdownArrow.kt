package bbb.audio.syntAX1.ui.component.sequencer.blank

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DropdownArrow(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.ArrowDropDown,
        contentDescription = null,
        tint = Color.Black.copy(alpha = 0.7f),
        modifier = modifier.size(24.dp)
    )
}