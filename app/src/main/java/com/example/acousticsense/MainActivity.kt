package com.example.acousticsense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.acousticsense.diagnostics.AndroidDeviceDiagnosticsCollector
import com.example.acousticsense.diagnostics.DiagnosticExporter
import com.example.acousticsense.diagnostics.DiagnosticsJsonSerializer
import com.example.acousticsense.diagnostics.DiagnosticsStateHolder
import com.example.acousticsense.ui.diagnostics.DiagnosticsScreen
import com.example.acousticsense.ui.theme.AcousticSenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val stateHolder = DiagnosticsStateHolder(AndroidDeviceDiagnosticsCollector(this))
        stateHolder.load()

        setContent {
            AcousticSenseTheme {
                var pendingJson by remember { mutableStateOf<String?>(null) }
                val createDocument = rememberLauncherForActivityResult(
                    ActivityResultContracts.CreateDocument("application/json")
                ) { uri ->
                    uri?.let { pendingJson?.let { json -> DiagnosticExporter(this).write(it, json) } }
                    pendingJson = null
                }

                DiagnosticsScreen(
                    state = stateHolder.state,
                    onRetry = stateHolder::load,
                    onExport = { diagnostics ->
                        pendingJson = DiagnosticsJsonSerializer.serialize(diagnostics)
                        createDocument.launch(DiagnosticExporter.suggestedFileName(diagnostics))
                    }
                )
            }
        }
    }
}
