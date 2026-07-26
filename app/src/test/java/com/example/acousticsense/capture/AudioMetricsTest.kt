package com.example.acousticsense.capture

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

class AudioMetricsTest {
    @Test fun emptyBufferHasFiniteFloor() {
        assertEquals(CaptureMetrics(), AudioMetrics.calculate(shortArrayOf()))
    }

    @Test fun digitalSilenceUsesDbfsFloor() {
        assertEquals(AudioMetrics.DBFS_FLOOR, AudioMetrics.calculate(ShortArray(8)).dbfs, 0.0)
    }

    @Test fun peakIsNormalized() {
        assertEquals(0.5, AudioMetrics.calculate(shortArrayOf(16_384)).peak, 0.00001)
    }

    @Test fun rmsUsesAllRequestedSamples() {
        assertEquals(0.5 / sqrt(2.0), AudioMetrics.calculate(shortArrayOf(16_384, 0)).rms, 0.00001)
    }

    @Test fun dbfsConversionUsesAmplitudeRatio() {
        assertEquals(-6.0206, AudioMetrics.calculate(shortArrayOf(16_384)).dbfs, 0.0001)
    }

    @Test fun pcm16ExtremesDoNotOverflow() {
        val metrics = AudioMetrics.calculate(shortArrayOf(Short.MIN_VALUE, Short.MAX_VALUE))
        assertEquals(1.0, metrics.peak, 0.0)
        assertEquals(0.99998, metrics.rms, 0.0001)
    }
}
