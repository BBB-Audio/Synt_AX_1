package bbb.audio.syntAX1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import bbb.audio.syntAX1.data.engine.puredata.PdEngineManager
import bbb.audio.syntAX1.ui.animation.SplashScreen
import bbb.audio.syntAX1.ui.navigation.AppNavigation
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
class MainActivity : ComponentActivity() {

    private lateinit var pdEngineManager: PdEngineManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            initPd()
        } else {
            handlePermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pdEngineManager = get()

        setContent {
            KoinAndroidContext {
                Synt_AX_1Theme {
                    var showSplashScreen by remember { mutableStateOf(true) }

                    if (showSplashScreen) {
                        SplashScreen(onTimeout = { showSplashScreen = false })
                    } else {
                        AppNavigation()
                    }
                }
            }
        }

        checkAndInitPd()
    }

    private fun checkAndInitPd() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                initPd()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun initPd() {
        lifecycleScope.launch {
            try {
                Log.i("MainActivity", "Starting PD initialization...")

                val initSuccess = pdEngineManager.initialize(
                    sampleRate = 44100,
                    inChannels = 0,
                    outChannels = 2,
                    ticksPerBuffer = 32,
                    restart = false
                )

                if (!initSuccess) {
                    handleInitError("Engine initialization failed")
                    return@launch
                }

                Log.i("MainActivity", "✓ PdEngineManager initialized")

                val clockPatchLoaded = pdEngineManager.loadPatch(
                    patchRawResId = R.raw.pd_clock,
                    patchFileName = "pd_clock.pd"
                )
                if (!clockPatchLoaded) {
                    handleInitError("Failed to load clock patch")
                    return@launch
                }
//region Temporary commented out for Präsentation
//                val samplerPatchLoaded = pdEngineManager.loadPatch(
//                    patchRawResId = R.raw.mvpsampler,
//                    patchFileName = "mvpsampler.pd"
//                )
//                if (!samplerPatchLoaded) {
//                    handleInitError("Failed to load sampler patch")
//                    return@launch
//                }

//endregion

                val synthPatchLoaded = pdEngineManager.loadPatch(
                    patchRawResId = R.raw.monolight,
                    patchFileName = "monolight.pd"
                )
                if (!synthPatchLoaded) {
                    handleInitError("Failed to load synth patch")
                    return@launch
                }
                delay(200)
                Log.i("MainActivity", "Setting up PD Receiver (AFTER all patches loaded)...")
                val receiverSetup = pdEngineManager.setupReceiver()
                if (!receiverSetup) {
                    handleInitError("Failed to setup PD receiver")
                    return@launch
                }

                Log.i("MainActivity", "✓ PD Receiver is now active and listening for callbacks")

                val audioStarted = pdEngineManager.startAudio()
                if (!audioStarted) {
                    handleInitError("Failed to start audio")
                    return@launch
                }
                Log.i("MainActivity", "✓ Patches (pd_clock.pd, mvpsynth.pd) loaded")


                //testPdReceiverIsActive()
                Log.i("MainActivity", "✓ Audio engine started. Sequencer playback is controlled by UI.")

                handleInitSuccess()

            } catch (e: Exception) {
                handleInitError(e.message ?: "Unknown error")
                Log.e("MainActivity", "Init exception", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pdEngineManager.releaseAudioEngine()
    }

    private fun handlePermissionDenied() {
        Log.e(TAG, "RECORD_AUDIO permission denied")
    }

    private fun handleInitSuccess() {
        Log.i(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.i(TAG, "✓ Clock Engine running")
        Log.i(TAG, "✓ PD initialized successfully")
        Log.i(TAG, "✓ Audio Engine running")
        Log.i(TAG, "✓ MIDI Systems ready (internal + USB)")
        Log.i(TAG, "✓ PD Receiver listening for clock ticks")
        Log.i(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }

    private fun handleInitError(message: String) {
        Log.e(TAG, "✗ Audio Engine init failed: $message")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}