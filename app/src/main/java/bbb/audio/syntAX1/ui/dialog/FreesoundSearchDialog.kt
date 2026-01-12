package bbb.audio.syntAX1.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bbb.audio.syntAX1.R
import bbb.audio.syntAX1.ui.component.audio.MediaPlayerController
import bbb.audio.syntAX1.ui.component.sampler.SoundResultItem
import bbb.audio.syntAX1.ui.viewmodel.FreesoundEvent
import bbb.audio.syntAX1.ui.viewmodel.FreesoundUiState

/**
 * Dialog component for searching Freesound, now with integrated rich error display
 * and using a single event callback for all user interactions.
 */
@Composable
fun FreesoundSearchDialog(
    uiState: FreesoundUiState,
    currentlyPlayingId: Int?,
    previewUrl: String?,
    onEvent: (FreesoundEvent) -> Unit
) {
    val context = LocalContext.current
    val mediaPlayerController = remember {
        MediaPlayerController(
            context = context,
            onCompletion = {
                onEvent(FreesoundEvent.StopPreview)
            }
        )
    }
    var searchQuery by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerController.release()
            onEvent(FreesoundEvent.StopPreview)
        }
    }

    LaunchedEffect(previewUrl) {
        previewUrl?.let { url ->
            mediaPlayerController.playPreview(url)
        }
    }

    AlertDialog(
        onDismissRequest = {
            mediaPlayerController.stop()
            if (uiState.error != null) {
                onEvent(FreesoundEvent.DismissError)
            } else {
                onEvent(FreesoundEvent.Dismiss)
            }
        },
        title = {
            Text(if (uiState.error != null) "An Error Occurred" else "Search Freesound")
        },
        text = {
            if (uiState.error != null) {
                // --- ERROR STATE UI ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.error),
                            contentDescription = "Error Icon",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.height(160.dp)
                        )
                        Text(
                            text = uiState.error.code.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red.copy(alpha = 0.8f),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error.message,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val solutionText = when (uiState.error.code) {
                        401, 403 -> "💡 Check your API Key in the settings."
                        404 -> "💡 The requested sound could not be found."
                        429 -> "💡 Too many requests. Please wait a moment."
                        0 -> "💡 Please check your internet connection."
                        -2 -> "💡 Please check your input (e.g., empty search)."
                        else -> "💡 An unexpected error occurred. Please try again."
                    }
                    Text(
                        text = solutionText,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // --- NORMAL SEARCH UI ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("e.g., 'kick drum'") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onEvent(FreesoundEvent.Search(searchQuery)) },
                            enabled = searchQuery.isNotBlank() && !uiState.isLoading
                        ) {
                            if (uiState.isLoading && uiState.sounds.isEmpty()) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            } else {
                                Text("Search")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        uiState.isLoading && uiState.sounds.isEmpty() -> {
                            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        uiState.sounds.isEmpty() && searchQuery.isNotEmpty() -> {
                            Text("No results for \"$searchQuery\"")
                        }
                        uiState.sounds.isNotEmpty() -> {
                            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                                items(uiState.sounds) { sound ->
                                    SoundResultItem(
                                        sound = sound,
                                        isPreviewPlaying = currentlyPlayingId == sound.id,
                                        onSelect = { onEvent(FreesoundEvent.SelectSound(sound)) },
                                        onPreviewClick = { onEvent(FreesoundEvent.TogglePreview(sound.id)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                mediaPlayerController.stop()
                if (uiState.error != null) {
                    onEvent(FreesoundEvent.DismissError)
                } else {
                    onEvent(FreesoundEvent.Dismiss)
                }
            }) {
                Text("Close")
            }
        }
    )
}
