package com.example.acousticsense.duplex

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID

class DuplexViewModel(
    private val engine: DuplexEngine,
    private val post: ((() -> Unit) -> Unit) = { Handler(Looper.getMainLooper()).post(it) },
    private val runner: LaboratorySessionRunner = LaboratorySessionRunner()
) : ViewModel() {
    var state by mutableStateOf(DuplexUiState()); private set

    fun updatePermission(granted: Boolean) {
        if (!granted && state.state in setOf(DuplexState.STARTED, DuplexState.STARTING)) stop(StopReason.PERMISSION_LOST)
        state = state.copy(permissionGranted = granted, lastError = if (granted) null else "Se requiere permiso de micrófono")
    }
    fun start() {
        if (!state.permissionGranted) { state = state.copy(state = DuplexState.ERROR, lastError = "Permiso de micrófono ausente"); return }
        if (state.state == DuplexState.STARTED || state.state == DuplexState.STARTING) return
        state = state.copy(state = DuplexState.STARTING, lastError = null, stopReason = null)
        post {
            if (engine.start()) state = state.copy(state = DuplexState.STARTED, engineSnapshot = engine.snapshot())
            else state = state.copy(state = DuplexState.ERROR, lastError = "No se pudieron abrir e iniciar ambos streams", engineSnapshot = engine.snapshot(), stopReason = StopReason.ERROR)
        }
    }
    fun stop(reason: StopReason = StopReason.USER_REQUEST) {
        if (state.state !in setOf(DuplexState.STARTED, DuplexState.STARTING, DuplexState.ERROR, DuplexState.DISCONNECTED)) return
        state = state.copy(state = DuplexState.STOPPING)
        val beforeClose = engine.snapshot()
        engine.stop()
        val afterClose = engine.snapshot()
        val retained = afterClose.copy(
            metrics = if (afterClose.metrics == EngineMetrics()) beforeClose.metrics else afterClose.metrics,
            actualConfiguration = if (afterClose.actualConfiguration == ActualAudioConfiguration()) beforeClose.actualConfiguration else afterClose.actualConfiguration
        )
        val event = SessionEvent(System.currentTimeMillis(), "engine_stopped", reason.name)
        state = state.copy(
            state = DuplexState.STOPPED, engineSnapshot = retained, stopReason = reason,
            session = state.session?.copy(finalEngineSnapshot = retained, actual = state.session?.actual ?: retained.actualConfiguration, events = state.session?.events.orEmpty() + event)
        )
    }
    fun pulse() { if (state.canPulse && !engine.emitPulse()) state = state.copy(lastError = "El pulso fue rechazado porque ambos streams no están iniciados") else refresh() }
    fun refresh() {
        val snapshot = engine.snapshot()
        state = state.copy(engineSnapshot = snapshot)
        if (snapshot.disconnected && state.state == DuplexState.STARTED) {
            state = state.copy(state = DuplexState.DISCONNECTED)
            stop(StopReason.STREAM_DISCONNECTED)
        }
    }
    fun beginGuided() { state = state.copy(guidedMode = true, session = runner.create(UUID.randomUUID().toString())) }
    fun nextTest() { state.session?.let { state = state.copy(session = runner.startNext(it, engine.snapshot())) } }
    fun completeTest(status: TestStatus, message: String? = null, cycles: List<StartStopCycle> = emptyList()) {
        state.session?.let { state = state.copy(session = runner.complete(it, engine.snapshot(), status, state.stopReason, message, cycles)) }
    }
    fun abortStability() { completeTest(TestStatus.ABORTED, "Prueba abortada por el usuario"); stop(StopReason.USER_REQUEST) }
    fun cancelGuided() { state.session?.let { state = state.copy(session = runner.cancel(it, engine.snapshot())) }; stop(StopReason.USER_REQUEST) }
    fun exportJson(appVersion: String, build: String, deviceJson: String): String {
        val session = state.session ?: runner.create(UUID.randomUUID().toString())
        val final = session.finalEngineSnapshot ?: state.engineSnapshot
        return SessionJsonSerializer.serialize(session.copy(finalEngineSnapshot = final, actual = session.actual ?: final.actualConfiguration), appVersion, build, deviceJson)
    }
    override fun onCleared() { stop(StopReason.ERROR); engine.close() }
}
