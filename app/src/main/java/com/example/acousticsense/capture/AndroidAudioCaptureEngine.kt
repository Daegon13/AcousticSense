package com.example.acousticsense.capture

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class AndroidAudioCaptureEngine(context: Context) : AudioCaptureEngine {
    private val appContext = context.applicationContext
    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "microphone-capture").apply { priority = Thread.NORM_PRIORITY + 1 }
    }
    private val active = AtomicBoolean(false)
    @Volatile private var recorder: AudioRecord? = null

    override fun start(listener: AudioCaptureEngine.Listener) {
        if (!active.compareAndSet(false, true)) return
        executor.execute { capture(listener) }
    }

    override fun stop() {
        if (!active.getAndSet(false)) return
        try {
            recorder?.stop()
        } catch (_: IllegalStateException) {
            // The worker owns release; stop is best-effort and idempotent.
        }
    }

    override fun close() {
        stop()
        executor.shutdown()
    }

    private fun capture(listener: AudioCaptureEngine.Listener) {
        var localRecorder: AudioRecord? = null
        var terminalError: CaptureError? = null
        try {
            if (!hasPermission()) throw CaptureException(CaptureError.PERMISSION_MISSING)
            val minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING)
            if (minBuffer <= 0) throw CaptureException(CaptureError.MINIMUM_BUFFER_UNAVAILABLE)
            val bufferBytes = maxOf(minBuffer, BLOCK_SAMPLES * Short.SIZE_BYTES)
            val choice = AudioSourceSelector.select(supportsUnprocessed())
            localRecorder = createRecorder(choice, bufferBytes)
                ?: throw CaptureException(CaptureError.NOT_INITIALIZED)
            recorder = localRecorder
            val selected = localRecorder.audioSource
            val configuration = CaptureConfiguration(
                requestedSource = choice.requested,
                selectedSource = AudioSourceSelector.name(selected),
                sampleRateHz = localRecorder.sampleRate,
                channelCount = localRecorder.channelCount,
                encoding = "PCM 16 bit",
                bufferSizeBytes = bufferBytes,
                recorderState = "INITIALIZED"
            )
            Log.i(TAG, "Capture configuration: $configuration")
            if (!active.get()) return
            try {
                localRecorder.startRecording()
            } catch (error: SecurityException) {
                throw CaptureException(CaptureError.PERMISSION_LOST, error)
            } catch (error: IllegalStateException) {
                throw CaptureException(CaptureError.START_FAILED, error)
            }
            if (localRecorder.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                throw CaptureException(CaptureError.START_FAILED)
            }
            listener.onStarted(configuration)
            readLoop(localRecorder, listener)
        } catch (error: CaptureException) {
            terminalError = error.kind
        } catch (error: SecurityException) {
            terminalError = CaptureError.PERMISSION_LOST
        } catch (error: Throwable) {
            Log.e(TAG, "Unexpected capture failure", error)
            terminalError = CaptureError.UNEXPECTED
        } finally {
            active.set(false)
            recorder = null
            try { localRecorder?.stop() } catch (_: IllegalStateException) {}
            localRecorder?.release()
            terminalError?.let { Log.e(TAG, "Capture stopped with error: $it") }
            terminalError?.let(listener::onError) ?: listener.onStopped()
        }
    }

    private fun readLoop(audioRecord: AudioRecord, listener: AudioCaptureEngine.Listener) {
        val samples = ShortArray(BLOCK_SAMPLES)
        var total = 0L
        val startedNanos = System.nanoTime()
        var lastUpdateNanos = 0L
        while (active.get()) {
            val read = audioRecord.read(samples, 0, samples.size, AudioRecord.READ_BLOCKING)
            if (read < 0) throw CaptureException(CaptureError.READ_FAILED)
            if (read == 0) continue
            total += read
            val now = System.nanoTime()
            if (now - lastUpdateNanos >= UI_UPDATE_NANOS) {
                val block = AudioMetrics.calculate(samples, read)
                listener.onMetrics(
                    block.copy(sampleCount = total, durationMillis = (now - startedNanos) / 1_000_000)
                )
                lastUpdateNanos = now
            }
        }
    }

    private fun createRecorder(choice: AudioSourceChoice, bufferBytes: Int): AudioRecord? {
        choice.candidates.forEach { source ->
            val candidate = try {
                @Suppress("MissingPermission")
                AudioRecord(source, SAMPLE_RATE, CHANNEL_MASK, ENCODING, bufferBytes)
            } catch (_: IllegalArgumentException) {
                null
            }
            if (candidate?.state == AudioRecord.STATE_INITIALIZED) return candidate
            candidate?.release()
        }
        return null
    }

    private fun supportsUnprocessed(): Boolean = appContext.getSystemService(AudioManager::class.java)
        ?.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED) == "true"

    private fun hasPermission() = ContextCompat.checkSelfPermission(
        appContext, Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    private class CaptureException(val kind: CaptureError, cause: Throwable? = null) : Exception(cause)

    companion object {
        private const val TAG = "AudioCaptureEngine"
        private const val SAMPLE_RATE = 48_000
        private const val BLOCK_SAMPLES = 2_048
        private const val UI_UPDATE_NANOS = 100_000_000L
        private const val CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO
        private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
    }
}
