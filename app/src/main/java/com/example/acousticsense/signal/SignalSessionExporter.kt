package com.example.acousticsense.signal

import android.content.Context
import android.net.Uri
import org.json.JSONObject

class SignalSessionExporter(private val context: Context) {
    fun write(uri: Uri, session: SignalSessionSnapshot, appVersion: String, build: String, device: JSONObject) {
        requireNotNull(context.contentResolver.openOutputStream(uri, "w")) { "No se pudo abrir el destino elegido" }.use {
            SignalSessionExport.writeZip(session,appVersion,build,device,it)
        }
    }
    companion object { fun fileName(id:String)="acoustic-sense-signal-session-$id.zip" }
}
