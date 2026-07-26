package com.example.acousticsense.signal

import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.log10
import kotlin.math.roundToInt
import kotlin.math.sqrt

object Pcm16Wav {
    fun sample(value: Float): Short {
        val clamped = value.coerceIn(-1f, 1f)
        return if (clamped <= -1f) Short.MIN_VALUE else (clamped * 32767f).roundToInt().toShort()
    }
    fun encode(samples: FloatArray, sampleRate: Int, channels: Int): ByteArray {
        require(sampleRate > 0 && channels > 0 && samples.size % channels == 0)
        val dataSize = samples.size * 2
        val out = ByteArrayOutputStream(44 + dataSize)
        fun ascii(s: String) = out.write(s.toByteArray(Charsets.US_ASCII))
        fun le16(v: Int) { out.write(v and 255); out.write(v ushr 8 and 255) }
        fun le32(v: Int) { repeat(4) { out.write(v ushr (8 * it) and 255) } }
        ascii("RIFF"); le32(36 + dataSize); ascii("WAVEfmt "); le32(16); le16(1); le16(channels)
        le32(sampleRate); le32(sampleRate * channels * 2); le16(channels * 2); le16(16); ascii("data"); le32(dataSize)
        samples.forEach { val s = sample(it).toInt(); le16(s) }
        return out.toByteArray()
    }
}

object SignalSessionExport {
    const val SCHEMA_VERSION = "2.0"
    private fun checksum(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    fun metrics(samples: FloatArray): EmissionMetrics {
        if (samples.isEmpty()) return EmissionMetrics()
        var sum=0.0; var squares=0.0; var min=1.0; var max=-1.0; var peak=0.0; var clipped=0
        samples.forEach { v -> val d=v.toDouble(); sum+=d; squares+=d*d; min=kotlin.math.min(min,d); max=kotlin.math.max(max,d); peak=kotlin.math.max(peak,kotlin.math.abs(d)); if(kotlin.math.abs(d)>=1.0)clipped++ }
        val rms=sqrt(squares/samples.size)
        return EmissionMetrics(peak,rms,if(rms>0)20*log10(rms) else -120.0,min,max,sum/samples.size,samples.size,clipped)
    }
    fun csv(session: SignalSessionSnapshot): String = buildString {
        appendLine("sessionId,chirpId,sequenceIndex,requestedAtMonotonicNanos,scheduledOutputFrame,firstOutputFrame,lastOutputFrame,captureStartInputFrame,captureEndInputFrame,startFrequencyHz,endFrequencyHz,durationMs,amplitude,window,inputSampleRateHz,outputSampleRateHz,capturedFrames,emittedFrames,inputPeak,inputRms,inputDbfs,clippedSamples,inputXruns,outputXruns,status,error")
        session.emissions.forEach { e -> appendLine(listOf(session.sessionId,e.chirpId,e.sequenceIndex,e.requestedAtMonotonicNanos,e.scheduledOutputFrame,e.firstOutputFrame,e.lastOutputFrame,e.captureStartInputFrame,e.captureEndInputFrame,e.configuration.startFrequencyHz,e.configuration.endFrequencyHz,e.configuration.durationMs,e.configuration.amplitude,e.configuration.window,e.inputSampleRateHz,e.outputSampleRateHz,e.capturedFrames,e.emittedFrames,e.metrics.peak,e.metrics.rms,e.metrics.dbfs,e.metrics.clippedSamples,e.inputXruns,e.outputXruns,e.status,e.error).joinToString(",") { csvField(it) }) }
    }
    fun csvField(value: Any?): String { val s=value?.toString().orEmpty(); return if(s.any{it==','||it=='"'||it=='\n'||it=='\r'}) "\"${s.replace("\"","\"\"")}\"" else s }
    fun writeZip(session: SignalSessionSnapshot, appVersion: String, build: String, device: JSONObject, output: OutputStream) {
        val input=Pcm16Wav.encode(session.capture,session.inputSampleRateHz,session.inputChannels)
        val reference=Pcm16Wav.encode(session.transmittedReference,session.outputSampleRateHz,session.outputChannels)
        val csv=csv(session).toByteArray()
        val files=mapOf("emissions.csv" to csv,"input-capture.wav" to input,"transmitted-reference.wav" to reference)
        val json=json(session,appVersion,build,device,files.mapValues{checksum(it.value)}).toByteArray()
        ZipOutputStream(output).use { zip -> (mapOf("session.json" to json)+files).forEach { (name,bytes)->zip.putNextEntry(ZipEntry(name));zip.write(bytes);zip.closeEntry() } }
    }
    fun json(s: SignalSessionSnapshot, appVersion:String, build:String, device:JSONObject, checksums:Map<String,String> = emptyMap()):String = JSONObject()
        .put("schemaVersion",SCHEMA_VERSION).put("sessionId",s.sessionId).put("appVersion",appVersion).put("commitBuild",build).put("device",device)
        .put("startedAtEpochMillis",s.startedAtEpochMillis).put("startedAtMonotonicNanos",s.startedAtMonotonicNanos).put("endedAtEpochMillis",s.endedAtEpochMillis)
        .put("chirpConfiguration",JSONObject(s.chirpConfiguration.toMap())).put("captureConfiguration",JSONObject(s.captureConfiguration.toMap()))
        .put("actualInput",JSONObject().put("sampleRateHz",s.inputSampleRateHz).put("channels",s.inputChannels)).put("actualOutput",JSONObject().put("sampleRateHz",s.outputSampleRateHz).put("channels",s.outputChannels))
        .put("emissions",JSONArray(s.emissions.map{JSONObject().put("chirpId",it.chirpId).put("sequenceIndex",it.sequenceIndex).put("status",it.status.name.lowercase()).put("capturedFrames",it.capturedFrames).put("emittedFrames",it.emittedFrames).put("captureOffsetFrames",it.captureOffsetFrames).put("referenceOffsetFrames",it.referenceOffsetFrames)}))
        .put("metrics",JSONObject().put("overflows",s.overflows).put("partialReads",s.partialReads).put("emptyReads",s.emptyReads)).put("stopReason",s.stopReason).put("events",JSONArray(s.events)).put("errors",JSONArray(s.errors))
        .put("files",JSONObject(checksums)).put("experimentalWarnings",JSONArray(listOf("Audio audible de laboratorio; no equivale a SPL.","Los relojes de entrada y salida no se presumen sincronizados.","No usar como ayuda de movilidad."))).toString()
    private fun ChirpConfiguration.toMap()=mapOf("startFrequencyHz" to startFrequencyHz,"endFrequencyHz" to endFrequencyHz,"durationMs" to durationMs,"amplitude" to amplitude,"window" to window.name,"sampleRateHz" to sampleRateHz,"frameCount" to frameCount)
    private fun CaptureConfiguration.toMap()=mapOf("preRollMs" to preRollMs,"postRollMs" to postRollMs)
}
