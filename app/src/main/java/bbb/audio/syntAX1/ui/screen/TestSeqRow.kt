package bbb.audio.syntAX1.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.R
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme

@Composable
fun TestSeqRow(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(4) {
            Image(
                painter = painterResource(id = R.drawable.no_free_para_seq_row),
                contentDescription = "Static Sequencer Row",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TestSeqRowPreview() {
    Synt_AX_1Theme {
        TestSeqRow()
    }
}
