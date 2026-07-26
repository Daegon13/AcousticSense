package com.example.acousticsense.duplex

import org.json.JSONObject

interface DuplexEngine : AutoCloseable {
    fun start(): Boolean
    fun stop()
    fun emitPulse(): Boolean
    fun snapshot(): EngineSnapshot
}

class NativeDuplexEngine : DuplexEngine {
    private var handle = nativeCreate()
    private var retainedSnapshot = EngineSnapshot()
    override fun start() = handle != 0L && nativeStart(handle)
    override fun stop() { if (handle != 0L) nativeStop(handle) }
    override fun emitPulse() = handle != 0L && nativePulse(handle)
    override fun snapshot(): EngineSnapshot {
        if (handle == 0L) return retainedSnapshot
        retainedSnapshot = parseSnapshot(nativeSnapshot(handle))
        return retainedSnapshot
    }
    override fun close() { if (handle != 0L) { retainedSnapshot = snapshot(); nativeDestroy(handle); handle = 0 } }

    private fun parseSnapshot(value: String): EngineSnapshot {
        val root = JSONObject(value)
        val metrics = root.optJSONObject("metrics") ?: JSONObject()
        val input = root.optJSONObject("input")
        val output = root.optJSONObject("output")
        fun nullableXrun(name: String) = if (!metrics.has(name) || metrics.isNull(name) || metrics.optLong(name, -1) < 0) null else metrics.getLong(name)
        return EngineSnapshot(
            running = root.optBoolean("running"), inputState = input?.optString("state", "Unavailable") ?: "Unavailable",
            outputState = output?.optString("state", "Unavailable") ?: "Unavailable", starts = root.optLong("starts"),
            stops = root.optLong("stops"), durationMillis = root.optLong("durationMillis"),
            metrics = EngineMetrics(metrics.optLong("framesRead"), metrics.optLong("framesWritten"), metrics.optLong("callbacks"), metrics.optLong("pulses"), nullableXrun("inputXruns"), nullableXrun("outputXruns"), metrics.optDouble("peak"), metrics.optDouble("rms")),
            actualConfiguration = ActualAudioConfiguration(output?.toString() ?: "null", input?.toString() ?: "null"),
            disconnected = root.optBoolean("disconnected"), lastError = root.optString("lastError").ifBlank { null }
        )
    }
    private external fun nativeCreate(): Long
    private external fun nativeDestroy(handle: Long)
    private external fun nativeStart(handle: Long): Boolean
    private external fun nativeStop(handle: Long)
    private external fun nativePulse(handle: Long): Boolean
    private external fun nativeSnapshot(handle: Long): String
    companion object { init { System.loadLibrary("acousticsense_audio") } }
}
