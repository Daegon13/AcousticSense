package com.example.acousticsense.diagnostics

object DiagnosticsJsonSerializer {
    fun serialize(value: DeviceDiagnostics): String = buildString {
        append("{\n")
        field("manufacturer", value.manufacturer)
        field("model", value.model)
        field("product", value.product)
        field("device", value.device)
        field("androidVersion", value.androidVersion)
        field("sdkLevel", value.sdkLevel)
        arrayField("supportedAbis", value.supportedAbis)
        field("appVersion", value.appVersion)
        nullableField("suggestedOutputSampleRate", value.suggestedOutputSampleRate)
        nullableField("framesPerBuffer", value.framesPerBuffer)
        devicesField("inputDevices", value.inputDevices)
        devicesField("outputDevices", value.outputDevices)
        append("  \"timestamp\": \"").append(escape(value.timestamp)).append("\"\n}")
    }

    private fun StringBuilder.field(name: String, value: String) {
        append("  \"").append(name).append("\": \"").append(escape(value)).append("\",\n")
    }

    private fun StringBuilder.field(name: String, value: Int) {
        append("  \"").append(name).append("\": ").append(value).append(",\n")
    }

    private fun StringBuilder.nullableField(name: String, value: Int?) {
        append("  \"").append(name).append("\": ").append(value ?: "null").append(",\n")
    }

    private fun StringBuilder.arrayField(name: String, values: List<String>) {
        append("  \"").append(name).append("\": [")
        append(values.joinToString(", ") { "\"${escape(it)}\"" })
        append("],\n")
    }

    private fun StringBuilder.devicesField(name: String, devices: List<AudioDeviceDiagnostic>) {
        append("  \"").append(name).append("\": [")
        devices.forEachIndexed { index, device ->
            if (index > 0) append(',')
            append("\n    {\"id\": ").append(device.id)
            append(", \"name\": \"").append(escape(device.name))
            append("\", \"type\": \"").append(escape(device.type))
            append("\", \"channelCount\": ").append(device.channelCount ?: "null").append('}')
        }
        if (devices.isNotEmpty()) append('\n').append("  ")
        append("],\n")
    }

    private fun escape(value: String): String = buildString {
        value.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> if (char.code < 0x20) append("\\u%04x".format(char.code)) else append(char)
            }
        }
    }
}
