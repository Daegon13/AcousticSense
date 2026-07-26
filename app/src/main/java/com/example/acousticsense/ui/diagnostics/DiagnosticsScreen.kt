package com.example.acousticsense.ui.diagnostics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.acousticsense.diagnostics.AudioDeviceDiagnostic
import com.example.acousticsense.diagnostics.DeviceDiagnostics
import com.example.acousticsense.diagnostics.DiagnosticsUiState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DiagnosticsScreen(
    state: DiagnosticsUiState,
    onRetry: () -> Unit,
    onExport: (DeviceDiagnostics) -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Diagnóstico del dispositivo") }) }) { padding ->
        when (state) {
            DiagnosticsUiState.Loading -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(Modifier.semantics { contentDescription = "Cargando diagnóstico" })
                Text("Recopilando información…", Modifier.padding(16.dp))
            }
            is DiagnosticsUiState.Error -> ErrorContent(state.message, padding, onRetry)
            is DiagnosticsUiState.Success -> DiagnosticsContent(state.diagnostics, padding, onExport)
        }
    }
}

@Composable
private fun DiagnosticsContent(
    diagnostics: DeviceDiagnostics,
    padding: PaddingValues,
    onExport: (DeviceDiagnostics) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SectionTitle("Sistema") }
        item { ValueCard("Fabricante", diagnostics.manufacturer) }
        item { ValueCard("Modelo", diagnostics.model) }
        item { ValueCard("Producto / dispositivo", "${diagnostics.product} / ${diagnostics.device}") }
        item { ValueCard("Android", "${diagnostics.androidVersion} (SDK ${diagnostics.sdkLevel})") }
        item { ValueCard("ABI soportadas", diagnostics.supportedAbis.ifEmpty { listOf("unknown") }.joinToString()) }
        item { ValueCard("Versión de la aplicación", diagnostics.appVersion) }
        item { SectionTitle("Audio reportado por Android") }
        item { ValueCard("Sample rate de salida sugerido", diagnostics.suggestedOutputSampleRate?.let { "$it Hz" } ?: "unavailable") }
        item { ValueCard("Frames por buffer", diagnostics.framesPerBuffer?.toString() ?: "unavailable") }
        item { SectionTitle("Dispositivos de entrada") }
        if (diagnostics.inputDevices.isEmpty()) item { ValueCard("Entradas", "unavailable") }
        items(diagnostics.inputDevices, key = { "input-${it.id}" }) { AudioDeviceCard(it) }
        item { SectionTitle("Dispositivos de salida") }
        if (diagnostics.outputDevices.isEmpty()) item { ValueCard("Salidas", "unavailable") }
        items(diagnostics.outputDevices, key = { "output-${it.id}" }) { AudioDeviceCard(it) }
        item { ValueCard("Timestamp", diagnostics.timestamp) }
        item {
            Button(
                onClick = { onExport(diagnostics) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).semantics {
                    contentDescription = "Exportar diagnóstico como archivo JSON"
                }
            ) { Text("Exportar JSON") }
        }
        item {
            Text(
                "Este diagnóstico informa datos declarados por Android; no valida capacidades acústicas.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AudioDeviceCard(device: AudioDeviceDiagnostic) = ValueCard(
    label = device.name,
    value = "Tipo: ${device.type}\nCanales: ${device.channelCount ?: "unavailable"}"
)

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
}

@Composable
private fun ValueCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
            contentDescription = "$label: $value"
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ErrorContent(message: String, padding: PaddingValues, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No se pudo obtener el diagnóstico", style = MaterialTheme.typography.titleLarge)
        Text(message, Modifier.padding(vertical = 16.dp))
        Button(onClick = onRetry, modifier = Modifier.heightIn(min = 48.dp)) { Text("Reintentar") }
    }
}
