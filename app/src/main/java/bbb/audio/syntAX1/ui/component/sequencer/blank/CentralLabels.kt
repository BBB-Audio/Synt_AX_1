package bbb.audio.syntAX1.ui.component.sequencer.blank

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CentralLabels(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        //Text("Free", style = MaterialTheme.typography.bodyLarge, color = Color.Black.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(18.dp))
        Spacer(modifier = Modifier.height(18.dp))
        Text("Length", style = MaterialTheme.typography.bodyLarge, color = Color.Black.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(18.dp))
        Text("Velo", style = MaterialTheme.typography.bodyLarge, color = Color.Black.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(20.dp))
        Text("Note", style = MaterialTheme.typography.bodyLarge, color = Color.Black.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(18.dp))
        Text("On/Off", style = MaterialTheme.typography.bodyLarge, color = Color.Black.copy(alpha = 0.8f))
    }
}