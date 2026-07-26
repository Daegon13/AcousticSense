package com.example.acousticsense.duplex

import org.junit.Assert.*
import org.junit.Test

class DuplexViewModelTest {
    private class Fake(var startsSuccessfully: Boolean = true) : DuplexEngine {
        var starts = 0; var stops = 0; var pulses = 0
        var value = EngineSnapshot(actualConfiguration = ActualAudioConfiguration("{\"sampleRate\":48000}", "{\"sampleRate\":48000}"))
        override fun start(): Boolean { starts++; if (startsSuccessfully) value = value.copy(running = true, inputState = "Started", outputState = "Started", starts = starts.toLong()); return startsSuccessfully }
        override fun stop() { stops++; value = value.copy(running = false, inputState = "Closed", outputState = "Closed", stops = stops.toLong()) }
        override fun emitPulse() = true.also { pulses++; value = value.copy(metrics = value.metrics.copy(pulses = pulses.toLong())) }
        override fun snapshot() = value
        override fun close() = Unit
    }
    private fun vm(engine: Fake) = DuplexViewModel(engine, { it() })
    @Test fun `duplicate start and stop are idempotent`() { val e=Fake(); val v=vm(e); v.updatePermission(true); v.start(); v.start(); v.stop(); v.stop(); assertEquals(1,e.starts); assertEquals(1,e.stops) }
    @Test fun `background stop preserves snapshot counters and configuration`() { val e=Fake(); val v=vm(e); v.updatePermission(true); v.start(); e.value=e.value.copy(metrics=EngineMetrics(framesRead=42,framesWritten=43,callbacks=4)); v.stop(StopReason.APP_BACKGROUND); assertEquals(42,v.state.engineSnapshot.metrics.framesRead); assertTrue(v.state.engineSnapshot.actualConfiguration.inputJson.contains("48000")); assertEquals(StopReason.APP_BACKGROUND,v.state.stopReason) }
    @Test fun `foreground permission update does not restart`() { val e=Fake(); val v=vm(e); v.updatePermission(true); v.start(); v.stop(StopReason.APP_BACKGROUND); v.updatePermission(true); assertEquals(1,e.starts); assertFalse(v.state.engineSnapshot.running) }
    @Test fun `on stop action invokes native stop exactly once`() { val e=Fake(); val v=vm(e); v.updatePermission(true); v.start(); v.stop(StopReason.APP_BACKGROUND); v.stop(StopReason.APP_BACKGROUND); assertEquals(1,e.stops) }
    @Test fun `permission loss records its real reason`() { val e=Fake(); val v=vm(e); v.updatePermission(true); v.start(); v.updatePermission(false); assertEquals(StopReason.PERMISSION_LOST,v.state.stopReason) }
}
