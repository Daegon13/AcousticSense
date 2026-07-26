package com.example.acousticsense.diagnostics

data class DeviceDiagnostics(
    val manufacturer: String,
    val model: String,
    val product: String,
    val device: String,
    val androidVersion: String,
    val sdkLevel: Int,
    val supportedAbis: List<String>,
    val appVersion: String,
    val suggestedOutputSampleRate: Int?,
    val framesPerBuffer: Int?,
    val inputDevices: List<AudioDeviceDiagnostic>,
    val outputDevices: List<AudioDeviceDiagnostic>,
    val timestamp: String
)

data class AudioDeviceDiagnostic(
    val id: Int,
    val name: String,
    val type: String,
    val channelCount: Int?
)

fun interface DeviceDiagnosticsCollector {
    fun collect(): DeviceDiagnostics
}
