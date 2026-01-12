package bbb.audio.syntAX1.ui.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bbb.audio.syntAX1.ui.component.common.ExpandableFab
import bbb.audio.syntAX1.ui.dialog.BpmControlDialog
import bbb.audio.syntAX1.ui.dialog.LoadPatternSheet
import bbb.audio.syntAX1.ui.dialog.SavePatternSheet
import bbb.audio.syntAX1.ui.dialog.SettingsContent
import bbb.audio.syntAX1.ui.screen.LibraryScreen
import bbb.audio.syntAX1.ui.screen.SampleScrubberScreen
import bbb.audio.syntAX1.ui.screen.SamplerScreen
import bbb.audio.syntAX1.ui.screen.SequencerScreen
import bbb.audio.syntAX1.ui.screen.SynthScreen
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme
import bbb.audio.syntAX1.ui.theme.Synt_AX_1_GirlieTheme
import bbb.audio.syntAX1.ui.viewmodel.LibraryViewModel
import bbb.audio.syntAX1.ui.viewmodel.SequencerViewModel
import bbb.audio.syntAX1.ui.viewmodel.SettingsViewModel
import bbb.audio.syntAX1.ui.viewmodel.SynthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun AppNavigation() {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()

    // The when block decides which theme Composable to call.
    // The content of the theme is the actual app UI.
    when (settingsState.themeName) {
        "Girlie" -> {
            Synt_AX_1_GirlieTheme {
                MainAppContent()
            }
        }
        else -> {
            Synt_AX_1Theme {
                MainAppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppContent() {
    val navController = rememberNavController()
    // Hoist ONLY what is truly shared across screens or needs to survive navigation.
    val sequencerViewModel: SequencerViewModel = koinViewModel()
    val synthViewModel: SynthViewModel = koinViewModel()

    // Central Snackbar state, owned by the top-level Scaffold.
    val snackbarHostState = remember { SnackbarHostState() }

    // State holders
    val sequencerUiState by sequencerViewModel.uiState.collectAsState()
    var fabExpanded by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var showBpmDialog by remember { mutableStateOf(false) }
    var settingsSheetOpen by remember { mutableStateOf(false) }
    var showSavePatternSheet by remember { mutableStateOf(false) }
    var showLoadPatternSheet by remember { mutableStateOf(false) }

    if (showBpmDialog) {
        BpmControlDialog(
            currentBpm = sequencerUiState.bpm,
            onBpmChanged = { sequencerViewModel.onBpmChanged(it) },
            onDismissRequest = { showBpmDialog = false }
        )
    }

    if (showSavePatternSheet) {
        SavePatternSheet(
            onDismissRequest = { showSavePatternSheet = false },
            onSaveClick = { patternName ->
                sequencerViewModel.savePattern(patternName, "")
                showSavePatternSheet = false
            }
        )
    }
    if (showLoadPatternSheet) {
        LoadPatternSheet(
            patterns = sequencerUiState.savedPatterns,
            onDismissRequest = { showLoadPatternSheet = false },
            onPatternSelected = { pattern ->
                sequencerViewModel.loadPattern(pattern.id)
                showLoadPatternSheet = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            val title = when (currentRoute) {
                "bbb.audio.syntAX1.ui.navigation.SequencerRoute" -> "Sequencer"
                "bbb.audio.syntAX1.ui.navigation.SamplerRoute" -> "Sampler"
                "bbb.audio.syntAX1.ui.navigation.ScrubberRoute" -> "Scrubber"
                "bbb.audio.syntAX1.ui.navigation.SynthRoute" -> "Synth"
                "bbb.audio.syntAX1.ui.navigation.LibraryRoute" -> "Library"
                else -> "Synt_AX_1" // Fallback title
            }
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { settingsSheetOpen = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }

                    if (settingsSheetOpen) {
                        ModalBottomSheet(
                            onDismissRequest = { settingsSheetOpen = false },
                            sheetState = rememberModalBottomSheetState(
                                skipPartiallyExpanded = true
                            ),
                            modifier = Modifier.fillMaxHeight(1f)
                        ) {
                            SettingsContent(
                                onDismiss = { settingsSheetOpen = false }
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    var menuExpanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Navigation Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sequencer") },
                            onClick = {
                                navController.navigate(SequencerRoute)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sampler") },
                            onClick = {
                                navController.navigate(SamplerRoute)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Scrubber") },
                            onClick = {
                                navController.navigate(ScrubberRoute)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Synth") },
                            onClick = {
                                navController.navigate(SynthRoute)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Library") },
                            onClick = {
                                navController.navigate(LibraryRoute)
                                menuExpanded = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(navController = navController, startDestination = SequencerRoute) {
                composable<SequencerRoute> {
                    SequencerScreen(
                        modifier = Modifier.fillMaxSize(),
                        sequencerViewModel = sequencerViewModel
                    )
                }
                composable<SamplerRoute> {
                    SamplerScreen(
                        modifier = Modifier.fillMaxSize(),
                        freesoundViewModel = koinViewModel(),
                        samplerViewModel = koinViewModel()
                    )
                }
                composable<ScrubberRoute> {
                    SampleScrubberScreen(
                        modifier = Modifier.fillMaxSize(),
                        sampleScrubberViewModel = koinViewModel()
                    )
                }
                composable<SynthRoute> {
                    SynthScreen(
                        modifier = Modifier.fillMaxSize(),
                        synthViewModel = synthViewModel,
                        sequencerViewModel = sequencerViewModel
                    )
                }
                composable<LibraryRoute> {
                    val libraryViewModel: LibraryViewModel = koinViewModel()
                    // This LaunchedEffect will now use the central snackbarHostState
                    LaunchedEffect(libraryViewModel.uiState.collectAsState().value.error) {
                        val error = libraryViewModel.uiState.value.error
                        if (error != null) {
                            snackbarHostState.showSnackbar(error)
                            libraryViewModel.clearMessages()
                        }
                    }
                    LibraryScreen(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHostState = snackbarHostState,
                        libraryViewModel = libraryViewModel,
                    )
                }
            }

            val coroutineScope = rememberCoroutineScope()
            var fabSize by remember { mutableStateOf(IntSize.Zero) }
            var fabAlignment by remember { mutableStateOf(Alignment.End) }

            val offsetX = remember { Animatable(0f) }
            val offsetY = remember { Animatable(0f) }

            ExpandableFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .onSizeChanged { fabSize = it }
                    .offset {
                        IntOffset(
                            offsetX.value.roundToInt(),
                            offsetY.value.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    val screenWidth = constraints.maxWidth.toFloat()
                                    val currentX = screenWidth - fabSize.width + offsetX.value

                                    if (currentX < (screenWidth - fabSize.width) / 2) {
                                        offsetX.animateTo(
                                            -(screenWidth - fabSize.width - (16.dp.toPx() * 2)),
                                            spring(stiffness = Spring.StiffnessMediumLow)
                                        )
                                        fabAlignment = Alignment.Start
                                    } else {
                                        offsetX.animateTo(
                                            0f,
                                            spring(stiffness = Spring.StiffnessMediumLow)
                                        )
                                        fabAlignment = Alignment.End
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                coroutineScope.launch {
                                    val newOffsetX = (offsetX.value + dragAmount.x).coerceIn(
                                        -(constraints.maxWidth.toFloat() - fabSize.width - 32.dp.toPx()),
                                        0f
                                    )
                                    val newOffsetY = (offsetY.value + dragAmount.y).coerceIn(
                                        -(constraints.maxHeight.toFloat() - fabSize.height - 32.dp.toPx()),
                                        0f
                                    )
                                    offsetX.snapTo(newOffsetX)
                                    offsetY.snapTo(newOffsetY)
                                }
                            }
                        )
                    },
                fabAlignment = fabAlignment,
                expanded = fabExpanded,
                onFabClick = { fabExpanded = !fabExpanded },
                isFreeLaneVisible = sequencerUiState.isFreeLaneVisible,
                onFreeLaneClick = { sequencerViewModel.onToggleFreeLaneVisibility() },
                isPlaying = sequencerUiState.playingStepIndex != null,
                onPlayClick = { sequencerViewModel.startStopPlayback() },
                isRecording = isRecording,
                onRecordClick = { isRecording = !isRecording },
                onBpmClick = { showBpmDialog = true },
                onSavePatternClick = { showSavePatternSheet = true },
                onLoadPatternClick = { showLoadPatternSheet = true }
            )
        }
    }
}