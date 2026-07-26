package com.example.acousticsense.capture

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptureViewModelTest {
    private val engine = FakeEngine()
    private val viewModel = CaptureViewModel(engine) { it() }

    @Test fun permissionTransitionsThroughExplanationToReady() {
        viewModel.showExplanation()
        assertEquals(PermissionState.EXPLANATION, viewModel.state.permission)
        viewModel.updatePermission(PermissionState.GRANTED)
        assertEquals(CaptureState.Ready, viewModel.state.capture)
    }

    @Test fun startWithoutPermissionTransitionsToVisibleError() {
        viewModel.start()
        assertEquals(CaptureState.Error(CaptureError.PERMISSION_MISSING), viewModel.state.capture)
        assertEquals(0, engine.starts)
    }

    @Test fun duplicateStartDoesNotCreateAnotherSession() {
        viewModel.updatePermission(PermissionState.GRANTED)
        viewModel.start()
        viewModel.start()
        assertEquals(1, engine.starts)
    }

    @Test fun stopWhileNotRecordingIsIdempotent() {
        viewModel.stop()
        assertEquals(0, engine.stops)
    }

    @Test fun stopReleasesLogicalSession() {
        viewModel.updatePermission(PermissionState.GRANTED)
        viewModel.start()
        viewModel.stop()
        assertEquals(1, engine.stops)
        assertEquals(CaptureState.Stopped, viewModel.state.capture)
    }

    @Test fun engineErrorReplacesRecordingState() {
        viewModel.updatePermission(PermissionState.GRANTED)
        viewModel.start()
        engine.listener!!.onError(CaptureError.READ_FAILED)
        assertEquals(CaptureState.Error(CaptureError.READ_FAILED), viewModel.state.capture)
    }

    @Test fun fakeEnginePublishesConfigurationAndMetrics() {
        viewModel.updatePermission(PermissionState.GRANTED)
        viewModel.start()
        engine.listener!!.onStarted(CONFIGURATION)
        engine.listener!!.onMetrics(CaptureMetrics(sampleCount = 42, durationMillis = 10))
        assertEquals(CONFIGURATION, viewModel.state.configuration)
        assertEquals(42, viewModel.state.metrics.sampleCount)
        assertTrue(viewModel.state.capture == CaptureState.Recording)
    }

    @Test fun losingPermissionStopsActiveCapture() {
        viewModel.updatePermission(PermissionState.GRANTED)
        viewModel.start()
        viewModel.updatePermission(PermissionState.DENIED)
        assertEquals(1, engine.stops)
        assertEquals(PermissionState.DENIED, viewModel.state.permission)
    }

    private class FakeEngine : AudioCaptureEngine {
        var starts = 0
        var stops = 0
        var listener: AudioCaptureEngine.Listener? = null
        override fun start(listener: AudioCaptureEngine.Listener) { starts++; this.listener = listener }
        override fun stop() { stops++ }
        override fun close() = Unit
    }

    companion object {
        private val CONFIGURATION = CaptureConfiguration("MIC", "MIC", 48_000, 1, "PCM 16 bit", 4096, "INITIALIZED")
    }
}
