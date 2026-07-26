package com.example.acousticsense

import android.Manifest
import android.os.Bundle
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

class MainActivity : ComponentActivity() {
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
        captureViewModel.updatePermission(permissionManager.currentState())
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> captureViewModel.stop()
                Lifecycle.Event.ON_RESUME -> captureViewModel.updatePermission(permissionManager.currentState())
                else -> Unit
            }
        })

        setContent {
            AcousticSenseTheme {
                var showDiagnostics by remember { mutableStateOf(false) }
                var pendingJson by remember { mutableStateOf<String?>(null) }
                val createDocument = rememberLauncherForActivityResult(
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

                if (showDiagnostics) {
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
                        onShowDiagnostics = { showDiagnostics = true }
                    )
                }
            }
        }
    }
}
