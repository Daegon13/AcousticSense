package com.example.acousticsense.duplex

enum class DuplexState { IDLE, STARTING, STARTED, STOPPING, STOPPED, ERROR, DISCONNECTED }
enum class TestStatus { PENDING, RUNNING, PASSED, FAILED, INCONCLUSIVE, SKIPPED, ABORTED }
enum class EvaluationMode { AUTOMATIC, OBSERVED, MANUAL_WITH_ASSERTIONS }
enum class StopReason {
    USER_REQUEST, APP_BACKGROUND, SCREEN_LOCKED, AUDIO_FOCUS_LOSS, AUDIO_ROUTE_CHANGED,
    STREAM_DISCONNECTED, PERMISSION_LOST, TEST_COMPLETED, ERROR
}

data class RequestedAudioConfiguration(
    val performanceMode: String = "LowLatency",
    val sharingMode: String = "Exclusive (fallback Shared)",
    val sampleRateHz: Int? = null,
    val channelCount: Int = 1,
    val format: String = "Float",
    val inputPreset: String = "Unprocessed (fallback VoiceRecognition)"
)

data class ActualAudioConfiguration(val outputJson: String = "null", val inputJson: String = "null")
data class EngineMetrics(
    val framesRead: Long = 0, val framesWritten: Long = 0, val callbacks: Long = 0,
    val pulses: Long = 0, val inputXruns: Long? = null, val outputXruns: Long? = null,
    val peak: Double = 0.0, val rms: Double = 0.0
) {
    operator fun minus(other: EngineMetrics) = copy(
        framesRead = (framesRead - other.framesRead).coerceAtLeast(0),
        framesWritten = (framesWritten - other.framesWritten).coerceAtLeast(0),
        callbacks = (callbacks - other.callbacks).coerceAtLeast(0),
        pulses = (pulses - other.pulses).coerceAtLeast(0),
        inputXruns = inputXruns?.let { current -> other.inputXruns?.let { (current - it).coerceAtLeast(0) } },
        outputXruns = outputXruns?.let { current -> other.outputXruns?.let { (current - it).coerceAtLeast(0) } }
    )
}
data class EngineSnapshot(
    val running: Boolean = false,
    val inputState: String = "Unavailable",
    val outputState: String = "Unavailable",
    val starts: Long = 0,
    val stops: Long = 0,
    val durationMillis: Long = 0,
    val metrics: EngineMetrics = EngineMetrics(),
    val actualConfiguration: ActualAudioConfiguration = ActualAudioConfiguration(),
    val disconnected: Boolean = false,
    val lastError: String? = null
)
data class TestAssertion(val name: String, val expected: String, val actual: String, val passed: Boolean)
data class StartStopCycle(val index: Int, val startSuccessful: Boolean, val stopSuccessful: Boolean, val durationMillis: Long, val error: String? = null, val resourcesReleased: Boolean)
data class SessionEvent(val timestampMillis: Long, val type: String, val detail: String)
data class TestDefinition(val id: String, val title: String, val minimumDurationMillis: Long = 0)
data class TestResult(
    val definition: TestDefinition,
    val status: TestStatus,
    val evaluationMode: EvaluationMode = EvaluationMode.AUTOMATIC,
    val startedAtMillis: Long? = null,
    val endedAtMillis: Long? = null,
    val metricsAtStart: EngineMetrics? = null,
    val metricsAtEnd: EngineMetrics? = null,
    val metricsDelta: EngineMetrics? = null,
    val actualAudioConfiguration: ActualAudioConfiguration? = null,
    val assertions: List<TestAssertion> = emptyList(),
    val stopReason: StopReason? = null,
    val errors: List<String> = emptyList(),
    val message: String? = null,
    val cycles: List<StartStopCycle> = emptyList()
) { val durationMillis: Long? get() = startedAtMillis?.let { start -> endedAtMillis?.minus(start) } }
data class SessionSummary(val passed: Int = 0, val failed: Int = 0, val inconclusive: Int = 0, val aborted: Int = 0, val finalStatus: TestStatus = TestStatus.PENDING)
data class LaboratorySession(
    val sessionId: String,
    val startedAtMillis: Long,
    val endedAtMillis: Long? = null,
    val requested: RequestedAudioConfiguration = RequestedAudioConfiguration(),
    val actual: ActualAudioConfiguration? = null,
    val finalEngineSnapshot: EngineSnapshot? = null,
    val results: List<TestResult> = defaultTests.map { TestResult(it, TestStatus.PENDING) },
    val events: List<SessionEvent> = emptyList(),
    val errors: List<String> = emptyList(),
    val summary: SessionSummary = SessionSummary()
)
const val STABILITY_DURATION_MILLIS = 300_000L
val defaultTests = listOf(
    TestDefinition("silent_session", "Sesión silenciosa", 10_000),
    TestDefinition("audible_pulse", "Pulso audible"),
    TestDefinition("repeated_start_stop", "Inicio y detención repetidos"),
    TestDefinition("stability_session", "Sesión de estabilidad", STABILITY_DURATION_MILLIS),
    TestDefinition("lifecycle_manual_check", "Chequeo de lifecycle")
)
data class DuplexUiState(
    val permissionGranted: Boolean = false,
    val state: DuplexState = DuplexState.IDLE,
    val engineSnapshot: EngineSnapshot = EngineSnapshot(),
    val lastError: String? = null,
    val stopReason: StopReason? = null,
    val session: LaboratorySession? = null,
    val guidedMode: Boolean = false
) { val canPulse get() = state == DuplexState.STARTED }
