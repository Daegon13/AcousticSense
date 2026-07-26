package com.example.acousticsense.duplex

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class DuplexLifecycleControllerTest {
    @Test fun `on stop records background exactly once and on start does not restart`() {
        val reasons=mutableListOf<StopReason>()
        val owner=object:LifecycleOwner { override val lifecycle=LifecycleRegistry(this) }
        val observer=DuplexLifecycleController({false}) { reasons += it }
        observer.onStop(owner); observer.onStart(owner)
        assertEquals(listOf(StopReason.APP_BACKGROUND),reasons)
    }
    @Test fun `locked screen has explicit reason`() { val reasons=mutableListOf<StopReason>(); val owner=object:LifecycleOwner { override val lifecycle:Lifecycle = LifecycleRegistry(this) }; DuplexLifecycleController({true}){reasons+=it}.onStop(owner); assertEquals(StopReason.SCREEN_LOCKED,reasons.single()) }
}
