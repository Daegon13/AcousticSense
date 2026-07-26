package com.example.acousticsense.duplex
import org.junit.Assert.*
import org.junit.Test
class LaboratorySessionRunnerTest{
 private var t=1L;private val r=LaboratorySessionRunner{t++}
 @Test fun `tests preserve required order and transitions`(){var s=r.create("id");assertEquals(defaultTests.map{it.id},s.results.map{it.definition.id});s=r.startNext(s);assertEquals("silent_session",s.results.first{it.status==TestStatus.RUNNING}.definition.id);s=r.complete(s,TestStatus.PASSED);s=r.startNext(s);assertEquals("audible_pulse",s.results.first{it.status==TestStatus.RUNNING}.definition.id)}
 @Test fun `inconclusive contributes to final summary`(){var s=r.create("id");repeat(5){s=r.startNext(s);s=r.complete(s,if(it==1)TestStatus.INCONCLUSIVE else TestStatus.PASSED)};assertEquals(TestStatus.INCONCLUSIVE,s.summary.finalStatus);assertEquals(1,s.summary.inconclusive)}
 @Test fun `failure does not corrupt remaining tests`(){var s=r.create("id");s=r.startNext(s);s=r.complete(s,TestStatus.FAILED,"busy");assertEquals(4,s.results.count{it.status==TestStatus.PENDING});assertEquals(1,s.errors.size)}
 @Test fun `cancel aborts running and pending tests`(){var s=r.startNext(r.create("id"));s=r.cancel(s);assertTrue(s.results.all{it.status==TestStatus.ABORTED});assertEquals(TestStatus.ABORTED,s.summary.finalStatus)}
}
