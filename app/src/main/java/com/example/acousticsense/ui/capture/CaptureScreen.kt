package com.example.acousticsense.ui.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.acousticsense.capture.CaptureState
import com.example.acousticsense.capture.CaptureUiState
import com.example.acousticsense.capture.DurationFormatter
import com.example.acousticsense.capture.PermissionState
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CaptureScreen(
    state: CaptureUiState,
    onExplainPermission: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onShowDiagnostics: () -> Unit,
    onShowDuplex: () -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Captura de micrófono") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedButton(onClick = onShowDuplex, modifier = actionModifier("Abrir laboratorio full-duplex experimental")) {
                    Text("Laboratorio full-duplex")
                }
            }
            item {
                Text("Captura básica controlada", style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { heading() })
            }
            item {
                Text("Esta función utiliza el micrófono únicamente durante una sesión iniciada por el usuario. En esta fase no guarda ni comparte grabaciones.")
            }
            item { PermissionCard(state.permission, onExplainPermission, onRequestPermission, onOpenSettings) }
            if (state.permission == PermissionState.GRANTED) {
                item { StatusCard(state) }
                item {
                    if (state.capture == CaptureState.Recording) {
                        Button(onClick = onStop, modifier = actionModifier("Detener captura de micrófono")) {
                            Text("Detener captura")
                        }
                    } else {
                        Button(onClick = onStart, modifier = actionModifier("Iniciar captura de micrófono")) {
                            Text("Iniciar captura")
                        }
                    }
                }
            }
            item {
                OutlinedButton(onClick = onShowDiagnostics, modifier = actionModifier("Abrir diagnóstico del dispositivo")) {
                    Text("Ver diagnóstico del dispositivo")
                }
            }
            item {
                Text("Nivel digital en dBFS; no es una medición física de presión sonora (SPL). No se reproduce audio ni se calculan distancias.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PermissionCard(
    permission: PermissionState,
    onExplain: () -> Unit,
    onRequest: () -> Unit,
    onSettings: () -> Unit
) {
    val message = when (permission) {
        PermissionState.NOT_REQUESTED -> "Permiso no solicitado. Antes de decidir, consultá por qué se necesita."
        PermissionState.EXPLANATION -> "El permiso permite leer niveles PCM solo mientras pulses Iniciar. No se conservan las muestras."
        PermissionState.GRANTED -> "Permiso concedido. La captura no comienza automáticamente."
        PermissionState.DENIED -> "Permiso rechazado. Podés revisar la explicación y volver a solicitarlo."
        PermissionState.PERMANENTLY_DENIED -> "Permiso rechazado permanentemente. Habilitalo desde la configuración de la aplicación."
        PermissionState.UNAVAILABLE -> "Acceso no disponible: Android no declara un micrófono en este dispositivo."
    }
    Card(Modifier.fillMaxWidth().semantics(mergeDescendants = true) { contentDescription = message }) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Acceso al micrófono", style = MaterialTheme.typography.titleMedium)
            Text(message)
            when (permission) {
                PermissionState.NOT_REQUESTED, PermissionState.DENIED ->
                    Button(onClick = onExplain, modifier = actionModifier("Ver explicación del permiso de micrófono")) { Text("Ver explicación") }
                PermissionState.EXPLANATION ->
                    Button(onClick = onRequest, modifier = actionModifier("Solicitar permiso de micrófono")) { Text("Continuar y solicitar permiso") }
                PermissionState.PERMANENTLY_DENIED ->
                    Button(onClick = onSettings, modifier = actionModifier("Abrir configuración de la aplicación")) { Text("Abrir configuración") }
                else -> Unit
            }
        }
    }
}

@Composable
private fun StatusCard(state: CaptureUiState) {
    val status = when (val capture = state.capture) {
        CaptureState.Ready -> "Listo"
        CaptureState.Recording -> "Grabando"
        CaptureState.Stopped -> "Detenido"
        is CaptureState.Error -> "Error: ${capture.error.userMessage}"
        null -> "No disponible"
    }
    val config = state.configuration
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Estado: $status", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            if (config != null) {
                Text("Fuente: ${config.requestedSource} solicitada / ${config.selectedSource} seleccionada")
                Text("Sample rate: ${config.sampleRateHz} Hz")
                Text("Canales: ${config.channelCount}")
                Text("Formato: ${config.encoding}")
                Text("Buffer: ${config.bufferSizeBytes} bytes")
                Text("AudioRecord: ${config.recorderState}")
            }
            Text("Duración: ${DurationFormatter.format(state.metrics.durationMillis)}")
            Text("Muestras capturadas: ${state.metrics.sampleCount}")
            Text("Pico: ${format(state.metrics.peak)}")
            Text("RMS: ${format(state.metrics.rms)}")
            Text("Nivel digital: ${format(state.metrics.dbfs)} dBFS")
            LinearProgressIndicator(
                progress = { state.metrics.peak.toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Medidor visual de nivel digital" }
            )
        }
    }
}

private fun actionModifier(description: String) = Modifier.fillMaxWidth().heightIn(min = 56.dp).semantics {
    contentDescription = description
}

private fun format(value: Double) = String.format(Locale.getDefault(), "%.2f", value)
