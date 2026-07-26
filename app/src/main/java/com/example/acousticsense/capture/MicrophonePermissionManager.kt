package com.example.acousticsense.capture

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MicrophonePermissionManager(private val activity: Activity) {
    private val preferences = activity.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun currentState(): PermissionState = when {
        !activity.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE) ->
            PermissionState.UNAVAILABLE
        ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED -> PermissionState.GRANTED
        !preferences.getBoolean(KEY_REQUESTED, false) -> PermissionState.NOT_REQUESTED
        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO) ->
            PermissionState.DENIED
        else -> PermissionState.PERMANENTLY_DENIED
    }

    fun markRequested() {
        preferences.edit().putBoolean(KEY_REQUESTED, true).apply()
    }

    fun openApplicationSettings() {
        activity.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", activity.packageName, null)
            }
        )
    }

    companion object {
        private const val PREFERENCES = "microphone_permission"
        private const val KEY_REQUESTED = "requested"
    }
}
