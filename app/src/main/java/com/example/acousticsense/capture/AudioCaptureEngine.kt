package com.example.acousticsense.capture

interface AudioCaptureEngine {
    interface Listener {
        fun onStarted(configuration: CaptureConfiguration)
        fun onMetrics(metrics: CaptureMetrics)
        fun onStopped()
        fun onError(error: CaptureError)
    }

    fun start(listener: Listener)
    fun stop()
    fun close()
}

data class AudioSourceChoice(val requested: String, val candidates: List<Int>)

object AudioSourceSelector {
    const val SOURCE_MIC = 1
    const val SOURCE_UNPROCESSED = 9

    fun select(unprocessedSupported: Boolean): AudioSourceChoice = if (unprocessedSupported) {
        AudioSourceChoice("UNPROCESSED", listOf(SOURCE_UNPROCESSED, SOURCE_MIC))
    } else {
        AudioSourceChoice("MIC", listOf(SOURCE_MIC))
    }

    fun name(source: Int): String = if (source == SOURCE_UNPROCESSED) "UNPROCESSED" else "MIC"
}
