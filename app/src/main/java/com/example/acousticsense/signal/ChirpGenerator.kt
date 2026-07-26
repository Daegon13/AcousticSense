package com.example.acousticsense.signal

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object ChirpGenerator {
    fun generate(configuration: ChirpConfiguration): FloatArray {
        configuration.validate()
        val count = configuration.frameCount
        val durationSeconds = configuration.durationMs / 1_000.0
        val slope = (configuration.endFrequencyHz - configuration.startFrequencyHz) / durationSeconds
        return FloatArray(count) { index ->
            val t = index.toDouble() / configuration.sampleRateHz
            val phase = 2.0 * PI * (configuration.startFrequencyHz * t + slope * t * t / 2.0)
            val hann = if (count == 1) 1.0 else 0.5 * (1.0 - cos(2.0 * PI * index / (count - 1)))
            (configuration.amplitude * hann * sin(phase)).toFloat().also {
                require(it.isFinite() && kotlin.math.abs(it) <= configuration.amplitude + 1e-7)
            }
        }
    }
}
