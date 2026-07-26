package com.example.acousticsense.duplex

object SessionJsonSerializer {
    const val SCHEMA_VERSION = "1.1"
    fun serialize(session: LaboratorySession, appVersion: String, build: String, deviceJson: String): String = buildString {
        append("{\"schemaVersion\":\"").append(SCHEMA_VERSION).append("\",\"sessionId\":\"").append(e(session.sessionId)).append("\",")
        append("\"timestamps\":{\"startedAtMillis\":").append(session.startedAtMillis).append(",\"endedAtMillis\":").append(session.endedAtMillis ?: "null").append("},")
        append("\"app\":{\"version\":\"").append(e(appVersion)).append("\",\"buildCommit\":\"").append(e(build)).append("\"},\"device\":").append(deviceJson).append(',')
        append("\"requestedConfiguration\":"); requested(session.requested); append(',')
        append("\"actualConfiguration\":"); actual(session.actual); append(',')
        append("\"finalEngineSnapshot\":"); snapshot(session.finalEngineSnapshot); append(",\"results\":[")
        session.results.forEachIndexed { index, result -> if (index > 0) append(','); result(result) }
        append("],\"events\":["); session.events.forEachIndexed { index, event -> if (index > 0) append(','); append("{\"timestampMillis\":${event.timestampMillis},\"type\":\"${e(event.type)}\",\"detail\":\"${e(event.detail)}\"}") }
        append("],\"errors\":["); session.errors.forEachIndexed { index, error -> if (index > 0) append(','); string(error) }
        append("],\"summary\":{\"passed\":${session.summary.passed},\"failed\":${session.summary.failed},\"inconclusive\":${session.summary.inconclusive},\"aborted\":${session.summary.aborted},\"finalStatus\":\"${session.summary.finalStatus.name.lowercase()}\"}}")
    }
    private fun StringBuilder.requested(value: RequestedAudioConfiguration) { append("{\"audioApi\":null,\"sampleRate\":").append(value.sampleRateHz ?: "null").append(",\"channelCount\":${value.channelCount},\"format\":\"${e(value.format)}\",\"performanceMode\":\"${e(value.performanceMode)}\",\"sharingMode\":\"${e(value.sharingMode)}\",\"inputPreset\":\"${e(value.inputPreset)}\"}") }
    private fun StringBuilder.actual(value: ActualAudioConfiguration?) { if (value == null) append("null") else append("{\"output\":").append(sanitize(value.outputJson)).append(",\"input\":").append(sanitize(value.inputJson)).append('}') }
    private fun StringBuilder.metrics(value: EngineMetrics?) {
        if (value == null) { append("null"); return }
        append("{\"framesRead\":${value.framesRead},\"framesWritten\":${value.framesWritten},\"callbacks\":${value.callbacks},\"pulses\":${value.pulses},\"inputXruns\":${value.inputXruns ?: "null"},\"outputXruns\":${value.outputXruns ?: "null"},\"peak\":${value.peak},\"rms\":${value.rms}}")
    }
    private fun StringBuilder.snapshot(value: EngineSnapshot?) {
        if (value == null) { append("null"); return }
        append("{\"running\":${value.running},\"inputState\":\"${e(value.inputState)}\",\"outputState\":\"${e(value.outputState)}\",\"starts\":${value.starts},\"stops\":${value.stops},\"durationMillis\":${value.durationMillis},\"disconnected\":${value.disconnected},\"lastError\":")
        nullableString(value.lastError); append(",\"metrics\":"); metrics(value.metrics); append(",\"actualConfiguration\":"); actual(value.actualConfiguration); append('}')
    }
    private fun StringBuilder.result(value: TestResult) {
        append("{\"id\":\"${e(value.definition.id)}\",\"startedAtMillis\":${value.startedAtMillis ?: "null"},\"endedAtMillis\":${value.endedAtMillis ?: "null"},\"durationMillis\":${value.durationMillis ?: "null"},\"status\":\"${value.status.name.lowercase()}\",\"evaluationMode\":\"${value.evaluationMode.name.lowercase()}\",\"metricsAtStart\":"); metrics(value.metricsAtStart)
        append(",\"metricsAtEnd\":"); metrics(value.metricsAtEnd); append(",\"metricsDelta\":"); metrics(value.metricsDelta); append(",\"actualAudioConfiguration\":"); actual(value.actualAudioConfiguration)
        append(",\"assertions\":["); value.assertions.forEachIndexed { index, a -> if (index > 0) append(','); append("{\"name\":\"${e(a.name)}\",\"expected\":\"${e(a.expected)}\",\"actual\":\"${e(a.actual)}\",\"passed\":${a.passed}}") }; append("],\"stopReason\":"); nullableString(value.stopReason?.name)
        append(",\"errors\":["); value.errors.forEachIndexed { index, error -> if (index > 0) append(','); string(error) }; append("],\"message\":"); nullableString(value.message); append('}')
    }
    private fun StringBuilder.string(value: String) { append('"').append(e(value)).append('"') }
    private fun StringBuilder.nullableString(value: String?) { if (value == null) append("null") else string(value) }
    private fun sanitize(json: String) = json.replace(Regex("\"xrunCount\"\\s*:\\s*-1"), "\"xrunCount\":null")
    private fun e(value: String) = value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
}
