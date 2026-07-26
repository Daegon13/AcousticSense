package com.example.acousticsense.duplex

import org.junit.Assert.*
import org.junit.Test

class LaboratorySessionRunnerTest {
    private var time=0L; private val runner=LaboratorySessionRunner { time }
    private val running = EngineSnapshot(true,"Started","Started",metrics=EngineMetrics(10,10,2,0),actualConfiguration=ActualAudioConfiguration("{}","{}"))
    private fun startAt(id:String): LaboratorySession { var s=runner.create("id"); val index=s.results.indexOfFirst{it.definition.id==id}; s=s.copy(results=s.results.mapIndexed{i,r->if(i<index)r.copy(status=TestStatus.SKIPPED)else r}); return runner.startNext(s,running) }
    @Test fun `silent session requires frames callbacks and duration`() { var s=startAt("silent_session"); time=10_000; s=runner.complete(s,running.copy(metrics=EngineMetrics(20,20,4,0)),TestStatus.PASSED); assertEquals(TestStatus.PASSED,s.results[0].status); assertTrue(s.results[0].assertions.all{it.passed}) }
    @Test fun `manual status cannot overwrite failed assertion`() { var s=startAt("silent_session"); time=10_000; s=runner.complete(s,running,TestStatus.PASSED); assertEquals(TestStatus.FAILED,s.results[0].status) }
    @Test fun `audible pulse requires emitted pulse`() { var s=startAt("audible_pulse"); s=runner.complete(s,running,TestStatus.PASSED); assertEquals(TestStatus.FAILED,s.results.first{it.definition.id=="audible_pulse"}.status) }
    @Test fun `repeated test requires every cycle`() { var s=startAt("repeated_start_stop"); s=runner.complete(s,running,TestStatus.PASSED,cycles=listOf(StartStopCycle(1,true,false,10,resourcesReleased=false))); assertEquals(TestStatus.FAILED,s.results.first{it.definition.id=="repeated_start_stop"}.status) }
    @Test fun `stability cannot pass early and can abort`() { var s=startAt("stability_session"); time=299_999; s=runner.complete(s,running,TestStatus.PASSED); assertEquals(TestStatus.FAILED,s.results.first{it.definition.id=="stability_session"}.status); s=startAt("stability_session"); s=runner.complete(s,running,TestStatus.ABORTED); assertEquals(TestStatus.ABORTED,s.results.first{it.definition.id=="stability_session"}.status) }
    @Test fun `stability passes at configured duration`() { var s=startAt("stability_session"); time=STABILITY_DURATION_MILLIS; s=runner.complete(s,running,TestStatus.PASSED); assertEquals(TestStatus.PASSED,s.results.first{it.definition.id=="stability_session"}.status) }
    @Test fun `lifecycle fails active and passes closed after background`() { var s=startAt("lifecycle_manual_check"); s=runner.complete(s,running,TestStatus.PASSED,StopReason.APP_BACKGROUND); assertEquals(TestStatus.FAILED,s.results.last().status); s=startAt("lifecycle_manual_check"); val closed=running.copy(running=false,inputState="Closed",outputState="Closed"); s=runner.complete(s,closed,TestStatus.PASSED,StopReason.APP_BACKGROUND); assertEquals(TestStatus.PASSED,s.results.last().status) }
    @Test fun `failure preserves previous result`() { var s=startAt("silent_session"); time=10_000; s=runner.complete(s,running,TestStatus.PASSED); assertEquals(TestStatus.FAILED,s.results[0].status); assertEquals(TestStatus.PENDING,s.results[1].status) }
}
