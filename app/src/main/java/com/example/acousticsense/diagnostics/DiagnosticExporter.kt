package com.example.acousticsense.diagnostics

import android.content.Context
import android.net.Uri

class DiagnosticExporter(private val context: Context) {
    fun write(destination: Uri, json: String) {
        context.contentResolver.openOutputStream(destination, "wt")?.bufferedWriter()?.use {
            it.write(json)
        } ?: error("Unable to open the selected destination")
    }

    companion object {
        fun suggestedFileName(diagnostics: DeviceDiagnostics): String =
            "acoustic-sense-diagnostic-${diagnostics.timestamp.replace(':', '-')}.json"
    }
}
