package com.example.acousticsense.diagnostics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiagnosticsJsonSerializerTest {
    private val diagnostics = DeviceDiagnostics(
        manufacturer = "Acme \"Mobile\"",
        model = "Model\\One",
        product = "product",
        device = "device",
        androidVersion = "14",
        sdkLevel = 34,
        supportedAbis = listOf("arm64-v8a"),
        appVersion = "1.0",
        suggestedOutputSampleRate = 48000,
        framesPerBuffer = null,
        inputDevices = listOf(AudioDeviceDiagnostic(1, "Mic\nmain", "built-in microphone", 2)),
        outputDevices = emptyList(),
        timestamp = "2026-07-26T12:00:00Z"
    )

    @Test
    fun serialize_includesValuesAndRepresentsUnavailableNumbersAsNull() {
        val json = DiagnosticsJsonSerializer.serialize(diagnostics)

        assertTrue(json.startsWith("{"))
        assertTrue(json.endsWith("}"))
        assertTrue(json.contains("\"manufacturer\": \"Acme \\\"Mobile\\\"\""))
        assertTrue(json.contains("\"model\": \"Model\\\\One\""))
        assertTrue(json.contains("\"framesPerBuffer\": null"))
        assertTrue(json.contains("\"outputDevices\": []"))
        assertTrue(json.contains("Mic\\nmain"))
    }

    @Test
    fun fileName_replacesTimestampColons() {
        assertEquals(
            "acoustic-sense-diagnostic-2026-07-26T12-00-00Z.json",
            DiagnosticExporter.suggestedFileName(diagnostics)
        )
    }
}
