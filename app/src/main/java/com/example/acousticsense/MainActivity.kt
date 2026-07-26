package com.example.acousticsense

import android.Manifest
import android.os.Bundle
import android.os.Build
import android.app.KeyguardManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.acousticsense.BuildConfig
import com.example.acousticsense.capture.AndroidAudioCaptureEngine
import com.example.acousticsense.capture.CaptureViewModel
import com.example.acousticsense.capture.MicrophonePermissionManager
import com.example.acousticsense.diagnostics.AndroidDeviceDiagnosticsCollector
import com.example.acousticsense.diagnostics.DiagnosticExporter
import com.example.acousticsense.diagnostics.DiagnosticsJsonSerializer
import com.example.acousticsense.diagnostics.DiagnosticsStateHolder
import com.example.acousticsense.ui.diagnostics.DiagnosticsScreen
import com.example.acousticsense.ui.capture.CaptureScreen
import com.example.acousticsense.ui.theme.AcousticSenseTheme
import com.example.acousticsense.duplex.DuplexViewModel
import com.example.acousticsense.duplex.NativeDuplexEngine
import com.example.acousticsense.duplex.DuplexLifecycleController
import com.example.acousticsense.duplex.StopReason
import com.example.acousticsense.ui.duplex.DuplexScreen
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private var duplexLifecycleObserver: LifecycleObserver? = null
    private var appLifecycleObserver: LifecycleObserver? = null
    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest
    private val duplexViewModel: DuplexViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DuplexViewModel(NativeDuplexEngine()) as T
        }
    }
    private val captureViewModel: CaptureViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CaptureViewModel(AndroidAudioCaptureEngine(applicationContext)) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val stateHolder = DiagnosticsStateHolder(AndroidDeviceDiagnosticsCollector(this))
        stateHolder.load()
        val permissionManager = MicrophonePermissionManager(this)
        audioManager = getSystemService(AudioManager::class.java)
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            .setOnAudioFocusChangeListener { change ->
                if (change == AudioManager.AUDIOFOCUS_LOSS || change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    duplexViewModel.stop(StopReason.AUDIO_FOCUS_LOSS)
                }
            }.build()
        captureViewModel.updatePermission(permissionManager.currentState())
        appLifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> captureViewModel.stop()
                Lifecycle.Event.ON_RESUME -> {
                    captureViewModel.updatePermission(permissionManager.currentState())
                    duplexViewModel.updatePermission(permissionManager.currentState() == com.example.acousticsense.capture.PermissionState.GRANTED)
                }
                else -> Unit
            }
        }.also(lifecycle::addObserver)
        val keyguard = getSystemService(KeyguardManager::class.java)
        duplexLifecycleObserver = DuplexLifecycleController(
            isScreenLocked = { keyguard?.isKeyguardLocked == true },
            stop = { reason -> abandonAudioFocus(); duplexViewModel.stop(reason) }
        ).also(lifecycle::addObserver)

        setContent {
            AcousticSenseTheme {
                var showDiagnostics by remember { mutableStateOf(false) }
                var showDuplex by remember { mutableStateOf(false) }
                var pendingJson by remember { mutableStateOf<String?>(null) }
                val createDocument = rememberLauncherForActivityResult(
                    ActivityResultContracts.CreateDocument("application/json")
                ) { uri ->
                    uri?.let { pendingJson?.let { json -> DiagnosticExporter(this).write(it, json) } }
                    pendingJson = null
                }
                val createDuplexDocument = rememberLauncherForActivityResult(
                    ActivityResultContracts.CreateDocument("application/json")
                ) { uri ->
                    uri?.let { pendingJson?.let { json -> DiagnosticExporter(this).write(it, json) } }
                    pendingJson = null
                }
                val requestMicrophone = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {
                    captureViewModel.updatePermission(permissionManager.currentState())
                }

                if (showDuplex) {
                    BackHandler { abandonAudioFocus(); duplexViewModel.stop(); showDuplex = false }
                    DuplexScreen(
                        state = duplexViewModel.state,
                        onBack = { abandonAudioFocus(); duplexViewModel.stop(); showDuplex = false },
                        onStart = { if (audioManager.requestAudioFocus(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) duplexViewModel.start() },
                        onStop = { abandonAudioFocus(); duplexViewModel.stop() },
                        onPulse = duplexViewModel::pulse,
                        onRefresh = duplexViewModel::refresh,
                        onBeginGuided = duplexViewModel::beginGuided,
                        onNext = duplexViewModel::nextTest,
                        onComplete = { duplexViewModel.completeTest(it) },
                        onCancel = duplexViewModel::cancelGuided,
                        onExport = {
                            val device = JSONObject()
                                .put("manufacturer", Build.MANUFACTURER)
                                .put("model", Build.MODEL)
                                .put("androidRelease", Build.VERSION.RELEASE)
                                .put("sdk", Build.VERSION.SDK_INT)
                                .put("supportedAbis", Build.SUPPORTED_ABIS.joinToString(","))
                                .toString()
                            pendingJson = duplexViewModel.exportJson(BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE, device)
                            createDuplexDocument.launch("acoustic-sense-full-duplex.json")
                        }
                    )
                } else if (showDiagnostics) {
                    BackHandler { showDiagnostics = false }
                    DiagnosticsScreen(
                        state = stateHolder.state,
                        onRetry = stateHolder::load,
                        onBack = { showDiagnostics = false },
                        onExport = { diagnostics ->
                            pendingJson = DiagnosticsJsonSerializer.serialize(diagnostics)
                            createDocument.launch(DiagnosticExporter.suggestedFileName(diagnostics))
                        }
                    )
                } else {
                    CaptureScreen(
                        state = captureViewModel.state,
                        onExplainPermission = captureViewModel::showExplanation,
                        onRequestPermission = {
                            permissionManager.markRequested()
                            requestMicrophone.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        onOpenSettings = permissionManager::openApplicationSettings,
                        onStart = captureViewModel::start,
                        onStop = captureViewModel::stop,
                        onShowDiagnostics = { showDiagnostics = true },
                        onShowDuplex = {
                            duplexViewModel.updatePermission(permissionManager.currentState() == com.example.acousticsense.capture.PermissionState.GRANTED)
                            showDuplex = true
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        abandonAudioFocus()
        duplexLifecycleObserver?.let(lifecycle::removeObserver)
        appLifecycleObserver?.let(lifecycle::removeObserver)
        duplexLifecycleObserver = null
        appLifecycleObserver = null
        super.onDestroy()
    }

    private fun abandonAudioFocus() {
        if (::audioManager.isInitialized && ::audioFocusRequest.isInitialized) audioManager.abandonAudioFocusRequest(audioFocusRequest)
    }
}
