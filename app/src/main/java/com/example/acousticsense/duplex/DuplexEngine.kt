package com.example.acousticsense.duplex
interface DuplexEngine : AutoCloseable { fun start(): Boolean; fun stop(); fun emitPulse(): Boolean; fun snapshotJson(): String }
class NativeDuplexEngine : DuplexEngine {
    private var handle = nativeCreate()
    override fun start() = handle != 0L && nativeStart(handle)
    override fun stop() { if(handle!=0L) nativeStop(handle) }
    override fun emitPulse() = handle != 0L && nativePulse(handle)
    override fun snapshotJson() = if(handle!=0L) nativeSnapshot(handle) else "{}"
    override fun close(){ if(handle!=0L){nativeDestroy(handle);handle=0} }
    private external fun nativeCreate():Long; private external fun nativeDestroy(handle:Long); private external fun nativeStart(handle:Long):Boolean; private external fun nativeStop(handle:Long); private external fun nativePulse(handle:Long):Boolean; private external fun nativeSnapshot(handle:Long):String
    companion object { init { System.loadLibrary("acousticsense_audio") } }
}
