package bbb.audio.syntAX1.ui.component.common

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.data.local.entity.Sample
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme
import kotlin.math.absoluteValue


/**
 * Horizontal carousel for browsing samples.
 * Supports swipe navigation and includes delete functionality.
 */
@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SampleCarousel(
    modifier: Modifier = Modifier,
    samples: List<Sample>,
    onSampleSelected: (Sample) -> Unit,
    onPreviewClick: (Sample) -> Unit = {},
    onDeleteSample: (Sample) -> Unit = {}
) {
    if (samples.isEmpty()) {
        Text(
            text = "No samples found",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
                .padding(16.dp),
        )
        return
    }

    val pagerState = rememberPagerState(pageCount = { samples.size })

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 48.dp),
        pageSpacing = -12.dp,
        modifier = modifier.height(330.dp)
    ) { pageIndex ->
        val sample = samples[pageIndex]
        val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction

        Box(
            modifier = Modifier
                .clickable { onSampleSelected(sample) }
                .graphicsLayer {
                    val scale = 1f - (pageOffset.absoluteValue * 0.25f)
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - (pageOffset.absoluteValue * 0.5f)
                }
        ) {
            SampleFloppyCard(
                sample = sample,
                onPreviewClick = onPreviewClick,
                onDeleteClick = onDeleteSample
            )
        }
    }
}

@Preview
@Composable
private fun SCPreview(
    showBackground: Boolean = true) {
    val previewSamples = listOf(
        Sample(id = 1, name = "Kick Drum", duration = 1.5f),
        Sample(id = 2, name = "Snare Hit", duration = 0.8f),
        Sample(id = 3, name = "Closed Hat", duration = 0.2f),
        Sample(id = 4, name = "Low Tom", duration = 2.1f),
        Sample(id = 5, name = "Cymbal Ride", duration = 4.5f)
    )

    Synt_AX_1Theme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ){
            SampleCarousel(
                samples = emptyList(),
                onSampleSelected = { },
                onDeleteSample = { }
            )
        }
    }
}@Preview
@Composable
private fun SCPreview2(
    showBackground: Boolean = true) {
    val previewSamples = listOf(
        Sample(id = 1, name = "Kick Drum", duration = 1.5f),
        Sample(id = 2, name = "Snare Hit", duration = 0.8f),
        Sample(id = 3, name = "Closed Hat", duration = 0.2f),
        Sample(id = 4, name = "Low Tom", duration = 2.1f),
        Sample(id = 5, name = "Cymbal Ride", duration = 4.5f)
    )

    Synt_AX_1Theme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ){
            SampleCarousel(
                samples = previewSamples,
                onSampleSelected = { },
                onDeleteSample = { }
            )
        }
    }
}