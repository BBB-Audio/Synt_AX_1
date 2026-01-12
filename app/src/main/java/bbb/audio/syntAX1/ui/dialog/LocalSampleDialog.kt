package bbb.audio.syntAX1.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import bbb.audio.syntAX1.data.local.entity.Sample
import bbb.audio.syntAX1.domain.PlaySamplePreviewUseCase
import bbb.audio.syntAX1.ui.component.common.SampleCarousel

@Composable
fun LocalSampleDialog(
    samples: List<Sample>,
    playSamplePreviewUseCase: PlaySamplePreviewUseCase,
    onDismiss: () -> Unit,
    onLocalSampleSelected: (Sample) -> Unit
) {
    DisposableEffect(Unit) {
        onDispose {
            playSamplePreviewUseCase.stop()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.height(400.dp)) {
            if (samples.isEmpty()) {
                Text("No samples available")
            } else {
                SampleCarousel(
                    samples = samples,
                    onSampleSelected = onLocalSampleSelected,
                    onPreviewClick = { sample ->
                        playSamplePreviewUseCase(sample)
                    }
                )
            }
        }
    }
}