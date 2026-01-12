package bbb.audio.syntAX1.ui.component.sequencer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.R
import bbb.audio.syntAX1.data.local.entity.Pattern
import bbb.audio.syntAX1.ui.theme.grapeNutsFont

/**
 * A carousel UI to select a saved pattern, with a floppy disk visual style.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PatternCarousel(
    patterns: List<Pattern>,
    onPatternSelected: (Pattern) -> Unit,
    modifier: Modifier = Modifier
) {
    if (patterns.isEmpty()) {
        Box(modifier = modifier.padding(8.dp), contentAlignment = Alignment.Center) {
            Text("No saved patterns.")
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { patterns.size })

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 64.dp),
        pageSpacing = -16.dp
    ) { page ->
        val pattern = patterns[page]
        PatternCard(
            pattern = pattern,
            onSelect = { onPatternSelected(pattern) }
        )
    }
}

@Composable
private fun PatternCard(
    pattern: Pattern,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 140.dp, height = 150.dp)
            .clickable(onClick = onSelect),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.disk_graphic),
            contentDescription = "Pattern Slot",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds)
//        SaveDisk(saveName = pattern.name, onNameChange = {})

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = pattern.name,
                fontFamily = grapeNutsFont,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${pattern.bpm.toInt()} BPM",
                fontFamily = grapeNutsFont,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
