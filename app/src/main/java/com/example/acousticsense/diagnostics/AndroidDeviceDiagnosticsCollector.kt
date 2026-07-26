package com.example.acousticsense.diagnostics

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import java.time.Instant

class AndroidDeviceDiagnosticsCollector(context: Context) : DeviceDiagnosticsCollector {
    private val appContext = context.applicationContext

    override fun collect(): DeviceDiagnostics {
        val audioManager = appContext.getSystemService(AudioManager::class.java)
        return DeviceDiagnostics(
            manufacturer = Build.MANUFACTURER.orUnknown(),
            model = Build.MODEL.orUnknown(),
            product = Build.PRODUCT.orUnknown(),
            device = Build.DEVICE.orUnknown(),
            androidVersion = Build.VERSION.RELEASE.orUnknown(),
            sdkLevel = Build.VERSION.SDK_INT,
            supportedAbis = Build.SUPPORTED_ABIS?.filter { it.isNotBlank() }.orEmpty(),
            appVersion = appVersion(),
            suggestedOutputSampleRate = audioManager
                ?.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).asPositiveIntOrNull(),
            framesPerBuffer = audioManager
                ?.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER).asPositiveIntOrNull(),
            inputDevices = audioManager?.getDevices(AudioManager.GET_DEVICES_INPUTS)
                ?.map(::audioDevice).orEmpty(),
            outputDevices = audioManager?.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                ?.map(::audioDevice).orEmpty(),
            timestamp = Instant.now().toString()
        )
    }

    @Suppress("DEPRECATION")
    private fun appVersion(): String = try {
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appContext.packageManager.getPackageInfo(
                appContext.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            appContext.packageManager.getPackageInfo(appContext.packageName, 0)
        }
        info.versionName.orUnknown()
    } catch (_: PackageManager.NameNotFoundException) {
        UNKNOWN
    }

    private fun audioDevice(device: AudioDeviceInfo) = AudioDeviceDiagnostic(
        id = device.id,
        name = device.productName?.toString().orUnknown(),
        type = AudioDeviceTypeFormatter.format(device.type),
        channelCount = device.channelCounts.filter { it > 0 }.maxOrNull()
    )
}

internal const val UNKNOWN = "unknown"

internal fun String?.orUnknown() = this?.takeIf { it.isNotBlank() } ?: UNKNOWN

internal fun String?.asPositiveIntOrNull() = this?.toIntOrNull()?.takeIf { it > 0 }
