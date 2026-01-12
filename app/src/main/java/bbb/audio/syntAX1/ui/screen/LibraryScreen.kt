package bbb.audio.syntAX1.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.ui.component.common.SampleCarousel
import bbb.audio.syntAX1.ui.viewmodel.LibraryViewModel

/**
 * Screen for browsing and managing the local sample library.
 * Displays samples in a carousel with delete functionality.
 */
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    libraryViewModel: LibraryViewModel
) {
    val uiState by libraryViewModel.uiState.collectAsState()
    val samples by libraryViewModel.samples.collectAsState()

    val context = LocalContext.current

// This side-effect handler now lives here, but acts on the central snackbarHostState.
    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            libraryViewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            libraryViewModel.clearMessages()
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Loading indicator
        if (uiState.isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error message
        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Success message
        uiState.successMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Sample carousel with delete functionality
        SampleCarousel(
            samples = samples,
            onSampleSelected = { sample ->
                libraryViewModel.shareSample(context, sample)
            },
            onPreviewClick = { sample ->
                libraryViewModel.playSamplePreview(sample)
            },
            onDeleteSample = { sample ->
                libraryViewModel.deleteSample(sample.id)
            }
        )
    }
}