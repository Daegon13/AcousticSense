package com.example.acousticsense.signal

import kotlin.math.roundToInt

enum class ChirpWindow { HANN }
enum class EmissionStatus { SCHEDULED, EMITTING, CAPTURED, COMPLETED, FAILED, ABORTED, INTERRUPTED }

data class ChirpConfiguration(
    val startFrequencyHz: Double = 4_000.0,
    val endFrequencyHz: Double = 12_000.0,
    val durationMs: Double = 10.0,
    val amplitude: Double = 0.05,
    val window: ChirpWindow = ChirpWindow.HANN,
    val sampleRateHz: Int = 48_000,
) {
    val frameCount: Int get() = (durationMs * sampleRateHz / 1_000.0).roundToInt()
    fun validate() {
        require(sampleRateHz > 0 && startFrequencyHz > 0.0)
        require(endFrequencyHz > startFrequencyHz && endFrequencyHz < sampleRateHz / 2.0)
        require(durationMs in 1.0..100.0)
        require(amplitude.isFinite() && amplitude in 0.0..MAX_AMPLITUDE)
        require(frameCount in 1..MAX_CHIRP_FRAMES)
    }
    companion object { const val MAX_AMPLITUDE = 0.05; const val MAX_CHIRP_FRAMES = 9_600 }
}

data class CaptureConfiguration(val preRollMs: Int = 50, val postRollMs: Int = 250) {
    fun validate() { require(preRollMs in 0..100); require(postRollMs in 0..500) }
    fun frames(sampleRate: Int, chirpFrames: Int) = preRollMs * sampleRate / 1_000 + chirpFrames + postRollMs * sampleRate / 1_000
}

data class EmissionMetrics(
    val peak: Double = 0.0, val rms: Double = 0.0, val dbfs: Double = -120.0,
    val minimum: Double = 0.0, val maximum: Double = 0.0, val mean: Double = 0.0,
    val sampleCount: Int = 0, val clippedSamples: Int = 0,
) { val clippingPercent get() = if (sampleCount == 0) 0.0 else clippedSamples * 100.0 / sampleCount }

data class ChirpEmission(
    val chirpId: String, val sequenceIndex: Int, val requestedAtMonotonicNanos: Long,
    val configuration: ChirpConfiguration, val scheduledOutputFrame: Long? = null,
    val firstOutputFrame: Long? = null, val lastOutputFrame: Long? = null,
    val outputCallbackIndex: Long? = null, val captureStartInputFrame: Long? = null,
    val captureEndInputFrame: Long? = null, val captureStartMonotonicNanos: Long? = null,
    val captureEndMonotonicNanos: Long? = null, val inputCallbackStart: Long? = null,
    val inputCallbackEnd: Long? = null, val emittedFrames: Int = 0, val capturedFrames: Int = 0,
    val inputSampleRateHz: Int, val outputSampleRateHz: Int, val metrics: EmissionMetrics = EmissionMetrics(),
    val inputXruns: Long? = null, val outputXruns: Long? = null,
    val status: EmissionStatus = EmissionStatus.SCHEDULED, val interruptions: List<String> = emptyList(),
    val error: String? = null, val captureOffsetFrames: Int = 0, val referenceOffsetFrames: Int = 0,
)

data class SignalSessionSnapshot(
    val sessionId: String, val startedAtEpochMillis: Long, val startedAtMonotonicNanos: Long,
    val endedAtEpochMillis: Long? = null, val chirpConfiguration: ChirpConfiguration,
    val captureConfiguration: CaptureConfiguration, val inputSampleRateHz: Int,
    val outputSampleRateHz: Int, val inputChannels: Int = 1, val outputChannels: Int = 1,
    val emissions: List<ChirpEmission> = emptyList(), val capture: FloatArray = floatArrayOf(),
    val transmittedReference: FloatArray = floatArrayOf(), val overflows: Int = 0,
    val partialReads: Long = 0, val emptyReads: Long = 0, val stopReason: String? = null,
    val events: List<String> = emptyList(), val errors: List<String> = emptyList(),
)

data class SeriesConfiguration(val count: Int, val intervalMs: Long) {
    fun validate() { require(count in 1..5); require(intervalMs >= 1_000) }
}
