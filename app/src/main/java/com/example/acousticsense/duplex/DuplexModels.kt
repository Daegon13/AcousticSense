package com.example.acousticsense.duplex

enum class DuplexState { IDLE, STARTING, STARTED, STOPPING, STOPPED, ERROR, DISCONNECTED }
enum class TestStatus { PENDING, RUNNING, PASSED, FAILED, INCONCLUSIVE, SKIPPED, ABORTED }
data class RequestedAudioConfiguration(val performanceMode: String="LowLatency", val sharingMode: String="Exclusive (fallback Shared)", val sampleRateHz: Int?=null, val channelCount: Int=1, val format: String="Float", val inputPreset: String="Unprocessed (fallback VoiceRecognition)")
data class ActualAudioConfiguration(val outputJson: String="{}", val inputJson: String="{}")
data class MetricSummary(val count: Long=0, val minimum: Double?=null, val maximum: Double?=null, val mean: Double?=null, val median: Double?=null, val p95: Double?=null, val totalXruns: Long=0, val totalFrames: Long=0, val totalCallbacks: Long=0)
data class SessionEvent(val timestampMillis: Long, val type: String, val detail: String)
data class TestDefinition(val id: String, val title: String, val requiresConfirmation: Boolean=false)
data class TestResult(val definition: TestDefinition, val status: TestStatus, val startedAtMillis: Long?=null, val endedAtMillis: Long?=null, val message: String?=null, val metrics: MetricSummary=MetricSummary())
data class SessionSummary(val passed: Int=0, val failed: Int=0, val inconclusive: Int=0, val aborted: Int=0, val finalStatus: TestStatus=TestStatus.PENDING)
data class LaboratorySession(val sessionId: String, val startedAtMillis: Long, val endedAtMillis: Long?=null, val requested: RequestedAudioConfiguration=RequestedAudioConfiguration(), val actual: ActualAudioConfiguration=ActualAudioConfiguration(), val results: List<TestResult> = defaultTests.map { TestResult(it, TestStatus.PENDING) }, val events: List<SessionEvent> = emptyList(), val errors: List<String> = emptyList(), val summary: SessionSummary=SessionSummary())
val defaultTests = listOf(TestDefinition("silent_session","Sesión silenciosa"),TestDefinition("audible_pulse","Pulso audible",true),TestDefinition("repeated_start_stop","Inicio y detención repetidos"),TestDefinition("stability_session","Sesión de estabilidad"),TestDefinition("lifecycle_manual_check","Chequeo manual de lifecycle",true))
data class DuplexUiState(val permissionGranted: Boolean=false, val state: DuplexState=DuplexState.IDLE, val nativeSnapshot: String="{}", val lastError: String?=null, val session: LaboratorySession?=null, val guidedMode: Boolean=false) { val canPulse get()=state==DuplexState.STARTED }
