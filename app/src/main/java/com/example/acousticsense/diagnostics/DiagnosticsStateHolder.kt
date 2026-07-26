package com.example.acousticsense.diagnostics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed interface DiagnosticsUiState {
    data object Loading : DiagnosticsUiState
    data class Success(val diagnostics: DeviceDiagnostics) : DiagnosticsUiState
    data class Error(val message: String) : DiagnosticsUiState
}

class DiagnosticsStateHolder(private val collector: DeviceDiagnosticsCollector) {
    var state: DiagnosticsUiState by mutableStateOf(DiagnosticsUiState.Loading)
        private set

    fun load() {
        state = DiagnosticsUiState.Loading
        state = try {
            DiagnosticsUiState.Success(collector.collect())
        } catch (error: RuntimeException) {
            DiagnosticsUiState.Error(error.message.orUnknown())
        }
    }
}
