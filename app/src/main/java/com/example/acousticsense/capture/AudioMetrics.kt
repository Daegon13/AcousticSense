package com.example.acousticsense.capture

import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.sqrt

object AudioMetrics {
    const val DBFS_FLOOR = -96.0
    private const val PCM_SCALE = 32768.0

    fun calculate(samples: ShortArray, count: Int = samples.size): CaptureMetrics {
        val safeCount = count.coerceIn(0, samples.size)
        if (safeCount == 0) return CaptureMetrics()

        var peak = 0.0
        var sumSquares = 0.0
        repeat(safeCount) { index ->
            val normalized = abs(samples[index].toDouble()) / PCM_SCALE
            peak = maxOf(peak, normalized)
            sumSquares += normalized * normalized
        }
        val rms = sqrt(sumSquares / safeCount)
        val dbfs = if (rms == 0.0) DBFS_FLOOR else maxOf(DBFS_FLOOR, 20.0 * log10(rms))
        return CaptureMetrics(peak = peak, rms = rms, dbfs = dbfs, sampleCount = safeCount.toLong())
    }
}
