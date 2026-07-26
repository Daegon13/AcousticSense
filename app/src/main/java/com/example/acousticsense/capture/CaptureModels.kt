package com.example.acousticsense.capture

enum class PermissionState {
    NOT_REQUESTED,
    EXPLANATION,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED,
    UNAVAILABLE
}

sealed interface CaptureState {
    data object Ready : CaptureState
    data object Recording : CaptureState
    data object Stopped : CaptureState
    data class Error(val error: CaptureError) : CaptureState
}

enum class CaptureError(val userMessage: String) {
    PERMISSION_MISSING("El permiso de micrófono no está concedido."),
    INVALID_CONFIGURATION("La configuración de captura no es válida en este teléfono."),
    MINIMUM_BUFFER_UNAVAILABLE("Android no informó un tamaño de buffer válido."),
    NOT_INITIALIZED("No se pudo inicializar el micrófono."),
    START_FAILED("No se pudo iniciar la captura; el micrófono puede estar ocupado."),
    READ_FAILED("Se interrumpió la lectura del micrófono."),
    PERMISSION_LOST("Se perdió el permiso de micrófono durante la sesión."),
    UNEXPECTED("Ocurrió un error inesperado durante la captura.")
}

data class CaptureConfiguration(
    val requestedSource: String,
    val selectedSource: String,
    val sampleRateHz: Int,
    val channelCount: Int,
    val encoding: String,
    val bufferSizeBytes: Int,
    val recorderState: String
)

data class CaptureMetrics(
    val peak: Double = 0.0,
    val rms: Double = 0.0,
    val dbfs: Double = AudioMetrics.DBFS_FLOOR,
    val sampleCount: Long = 0,
    val durationMillis: Long = 0
)

data class CaptureUiState(
    val permission: PermissionState = PermissionState.NOT_REQUESTED,
    val capture: CaptureState? = null,
    val configuration: CaptureConfiguration? = null,
    val metrics: CaptureMetrics = CaptureMetrics()
)

object DurationFormatter {
    fun format(durationMillis: Long): String {
        val totalSeconds = durationMillis.coerceAtLeast(0) / 1_000
        return "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
    }
}
