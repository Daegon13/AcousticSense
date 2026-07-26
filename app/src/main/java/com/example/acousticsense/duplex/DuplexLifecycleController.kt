package com.example.acousticsense.duplex

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/** One Activity-owned observer; foreground transitions never start audio. */
class DuplexLifecycleController(
    private val isScreenLocked: () -> Boolean,
    private val stop: (StopReason) -> Unit
) : DefaultLifecycleObserver {
    override fun onStop(owner: LifecycleOwner) {
        stop(if (isScreenLocked()) StopReason.SCREEN_LOCKED else StopReason.APP_BACKGROUND)
    }
}
