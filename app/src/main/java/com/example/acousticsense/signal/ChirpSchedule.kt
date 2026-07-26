package com.example.acousticsense.signal

/** Bounded, deterministic schedule. The UI must confirm before calling [start]. */
class ChirpSchedule(private val nowMillis: () -> Long = System::currentTimeMillis) {
    var remaining: Int = 0; private set
    var active = false; private set
    private var intervalMs = 1_000L
    private var lastEmissionAt: Long? = null
    fun start(configuration: SeriesConfiguration) { configuration.validate(); remaining=configuration.count; intervalMs=configuration.intervalMs; lastEmissionAt=null; active=true }
    fun ready(): Boolean = active && remaining > 0 && (lastEmissionAt?.let { nowMillis()-it>=intervalMs } ?: true)
    fun consume(): Boolean { if(!ready())return false; remaining--;lastEmissionAt=nowMillis();if(remaining==0)active=false;return true }
    fun cancel() { active=false;remaining=0;lastEmissionAt=null }
    fun onBackground()=cancel()
    fun onAudioFocusLoss()=cancel()
}
