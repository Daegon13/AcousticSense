package com.example.acousticsense.duplex
import org.junit.Assert.*
import org.junit.Test
class DuplexViewModelTest {
 private class Fake(var starts:Boolean=true):DuplexEngine{var startCalls=0;var stops=0;var pulses=0;override fun start()=starts.also{startCalls++};override fun stop(){stops++};override fun emitPulse()=true.also{pulses++};override fun snapshotJson()="{\"running\":true}";override fun close()=Unit}
 private fun vm(e:Fake)=DuplexViewModel(e,{it()})
 @Test fun `start without permission reports error`(){val v=vm(Fake());v.start();assertEquals(DuplexState.ERROR,v.state.state)}
 @Test fun `duplicate start and stop are idempotent`(){val e=Fake();val v=vm(e);v.updatePermission(true);v.start();v.start();assertEquals(1,e.startCalls);v.stop();v.stop();assertEquals(1,e.stops)}
 @Test fun `opening failure is visible`(){val v=vm(Fake(false));v.updatePermission(true);v.start();assertEquals(DuplexState.ERROR,v.state.state);assertNotNull(v.state.lastError)}
 @Test fun `pulse only while started`(){val e=Fake();val v=vm(e);v.pulse();assertEquals(0,e.pulses);v.updatePermission(true);v.start();v.pulse();assertEquals(1,e.pulses)}
 @Test fun `permission loss stops session`(){val e=Fake();val v=vm(e);v.updatePermission(true);v.start();v.updatePermission(false);assertEquals(DuplexState.STOPPED,v.state.state)}
}
