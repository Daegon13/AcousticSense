package com.example.acousticsense.capture

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CaptureViewModel(
    private val engine: AudioCaptureEngine,
    private val postToUi: ((() -> Unit) -> Unit) = { action -> Handler(Looper.getMainLooper()).post(action) }
) : ViewModel(), AudioCaptureEngine.Listener {
    var state: CaptureUiState by mutableStateOf(CaptureUiState())
        private set

    fun updatePermission(permission: PermissionState) {
        if (permission != PermissionState.GRANTED && state.capture == CaptureState.Recording) stop()
        state = state.copy(
            permission = permission,
            capture = when {
                permission == PermissionState.GRANTED &&
                    (state.capture == null || state.capture == CaptureState.Error(CaptureError.PERMISSION_MISSING)) ->
                    CaptureState.Ready
                permission != PermissionState.GRANTED -> null
                else -> state.capture
            }
        )
    }

    fun showExplanation() {
        if (state.permission == PermissionState.NOT_REQUESTED || state.permission == PermissionState.DENIED) {
            state = state.copy(permission = PermissionState.EXPLANATION)
        }
    }

    fun start() {
        if (state.permission != PermissionState.GRANTED) {
            state = state.copy(capture = CaptureState.Error(CaptureError.PERMISSION_MISSING))
            return
        }
        if (state.capture == CaptureState.Recording) return
        state = state.copy(capture = CaptureState.Recording, metrics = CaptureMetrics())
        engine.start(this)
    }

    fun stop() {
        if (state.capture != CaptureState.Recording) return
        engine.stop()
        state = state.copy(capture = CaptureState.Stopped)
    }

    override fun onStarted(configuration: CaptureConfiguration) = dispatch {
        state = state.copy(capture = CaptureState.Recording, configuration = configuration)
    }

    override fun onMetrics(metrics: CaptureMetrics) = dispatch {
        if (state.capture == CaptureState.Recording) state = state.copy(metrics = metrics)
    }

    override fun onStopped() = dispatch {
        if (state.capture == CaptureState.Recording) state = state.copy(capture = CaptureState.Stopped)
    }

    override fun onError(error: CaptureError) = dispatch {
        state = state.copy(capture = CaptureState.Error(error))
    }

    private fun dispatch(action: () -> Unit) = postToUi(action)

    override fun onCleared() {
        engine.close()
    }
}
