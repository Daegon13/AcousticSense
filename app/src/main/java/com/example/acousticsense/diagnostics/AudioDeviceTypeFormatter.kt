package com.example.acousticsense.diagnostics

import android.annotation.SuppressLint
import android.media.AudioDeviceInfo

object AudioDeviceTypeFormatter {
    @SuppressLint("InlinedApi")
    fun format(type: Int): String = when (type) {
        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> "built-in earpiece"
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "built-in speaker"
        AudioDeviceInfo.TYPE_WIRED_HEADSET -> "wired headset"
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "wired headphones"
        AudioDeviceInfo.TYPE_LINE_ANALOG -> "analog line"
        AudioDeviceInfo.TYPE_LINE_DIGITAL -> "digital line"
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> "Bluetooth SCO"
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "Bluetooth A2DP"
        AudioDeviceInfo.TYPE_HDMI -> "HDMI"
        AudioDeviceInfo.TYPE_HDMI_ARC -> "HDMI ARC"
        AudioDeviceInfo.TYPE_USB_DEVICE -> "USB device"
        AudioDeviceInfo.TYPE_USB_ACCESSORY -> "USB accessory"
        AudioDeviceInfo.TYPE_DOCK -> "dock"
        AudioDeviceInfo.TYPE_FM -> "FM"
        AudioDeviceInfo.TYPE_BUILTIN_MIC -> "built-in microphone"
        AudioDeviceInfo.TYPE_FM_TUNER -> "FM tuner"
        AudioDeviceInfo.TYPE_TV_TUNER -> "TV tuner"
        AudioDeviceInfo.TYPE_TELEPHONY -> "telephony"
        AudioDeviceInfo.TYPE_AUX_LINE -> "auxiliary line"
        AudioDeviceInfo.TYPE_IP -> "IP"
        AudioDeviceInfo.TYPE_BUS -> "bus"
        AudioDeviceInfo.TYPE_USB_HEADSET -> "USB headset"
        AudioDeviceInfo.TYPE_HEARING_AID -> "hearing aid"
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER_SAFE -> "safe built-in speaker"
        AudioDeviceInfo.TYPE_REMOTE_SUBMIX -> "remote submix"
        else -> "unknown ($type)"
    }
}
