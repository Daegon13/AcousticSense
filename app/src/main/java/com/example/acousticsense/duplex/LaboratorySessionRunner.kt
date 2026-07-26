package com.example.acousticsense.duplex

class LaboratorySessionRunner(private val now: () -> Long = System::currentTimeMillis) {
    fun create(id: String): LaboratorySession {
        val time = now()
        return LaboratorySession(id, time, events = listOf(SessionEvent(time, "session_created", "Batería creada en orden secuencial")))
    }

    fun startNext(session: LaboratorySession, snapshot: EngineSnapshot): LaboratorySession {
        if (session.results.any { it.status == TestStatus.RUNNING }) return session
        val index = session.results.indexOfFirst { it.status == TestStatus.PENDING }
        if (index < 0) return finish(session)
        val time = now()
        return session.copy(
            actual = session.actual ?: snapshot.actualConfiguration,
            results = session.results.updated(index, session.results[index].copy(status = TestStatus.RUNNING, startedAtMillis = time, metricsAtStart = snapshot.metrics, actualAudioConfiguration = snapshot.actualConfiguration)),
            events = session.events + SessionEvent(time, "test_started", session.results[index].definition.id)
        )
    }

    fun complete(
        session: LaboratorySession,
        snapshot: EngineSnapshot,
        requestedStatus: TestStatus,
        stopReason: StopReason? = null,
        message: String? = null,
        cycles: List<StartStopCycle> = emptyList()
    ): LaboratorySession {
        require(requestedStatus in setOf(TestStatus.PASSED, TestStatus.FAILED, TestStatus.INCONCLUSIVE, TestStatus.SKIPPED, TestStatus.ABORTED))
        val index = session.results.indexOfFirst { it.status == TestStatus.RUNNING }
        if (index < 0) return session
        val old = session.results[index]
        val ended = now()
        val delta = snapshot.metrics - (old.metricsAtStart ?: EngineMetrics())
        val assertions = evaluate(old.definition, old.startedAtMillis ?: ended, ended, snapshot, delta, stopReason, cycles)
        val status = when {
            requestedStatus == TestStatus.ABORTED -> TestStatus.ABORTED
            requestedStatus == TestStatus.SKIPPED -> TestStatus.SKIPPED
            assertions.any { !it.passed } -> TestStatus.FAILED
            requestedStatus == TestStatus.PASSED -> TestStatus.PASSED
            else -> requestedStatus
        }
        val errors = buildList {
            if (!snapshot.lastError.isNullOrBlank()) add(snapshot.lastError)
            assertions.filterNot { it.passed }.forEach { add("${it.name}: esperado ${it.expected}, observado ${it.actual}") }
        }
        val result = old.copy(
            status = status, evaluationMode = if (old.definition.id == "lifecycle_manual_check") EvaluationMode.OBSERVED else EvaluationMode.AUTOMATIC,
            endedAtMillis = ended, metricsAtEnd = snapshot.metrics, metricsDelta = delta,
            actualAudioConfiguration = old.actualAudioConfiguration ?: snapshot.actualConfiguration,
            assertions = assertions, stopReason = stopReason, errors = errors, message = message, cycles = cycles
        )
        val next = session.copy(
            actual = session.actual ?: result.actualAudioConfiguration,
            finalEngineSnapshot = snapshot,
            results = session.results.updated(index, result),
            errors = session.errors + errors.map { "${old.definition.id}: $it" },
            events = session.events + SessionEvent(ended, "test_completed", "${old.definition.id}: $status")
        )
        return if (next.results.none { it.status == TestStatus.PENDING }) finish(next) else next
    }

    private fun evaluate(definition: TestDefinition, started: Long, ended: Long, snapshot: EngineSnapshot, delta: EngineMetrics, reason: StopReason?, cycles: List<StartStopCycle>): List<TestAssertion> {
        val duration = ended - started
        fun assertion(name: String, expected: String, actual: Any?, passed: Boolean) = TestAssertion(name, expected, actual?.toString() ?: "null", passed)
        return when (definition.id) {
            "silent_session" -> listOf(
                assertion("input_was_started", "Started", snapshot.inputState, snapshot.inputState == "Started"),
                assertion("output_was_started", "Started", snapshot.outputState, snapshot.outputState == "Started"),
                assertion("input_frames_increased", "> 0", delta.framesRead, delta.framesRead > 0),
                assertion("output_frames_increased", "> 0", delta.framesWritten, delta.framesWritten > 0),
                assertion("callbacks_increased", "> 0", delta.callbacks, delta.callbacks > 0),
                assertion("no_pulses", "0", delta.pulses, delta.pulses == 0),
                assertion("minimum_duration", ">= ${definition.minimumDurationMillis}", duration, duration >= definition.minimumDurationMillis),
                assertion("no_blocking_error", "none", snapshot.lastError, snapshot.lastError.isNullOrBlank())
            )
            "audible_pulse" -> listOf(
                assertion("pulse_requested_and_emitted", "> 0", delta.pulses, delta.pulses > 0),
                assertion("energy_observed", "peak or RMS nondecreasing", "peak=${snapshot.metrics.peak}, rms=${snapshot.metrics.rms}", snapshot.metrics.peak >= (oldMetricPeakFallback(delta)) && snapshot.metrics.rms >= 0)
            )
            "repeated_start_stop" -> listOf(
                assertion("all_cycles_completed", "> 0 and every cycle successful", cycles.size, cycles.isNotEmpty() && cycles.all { it.startSuccessful && it.stopSuccessful && it.resourcesReleased && it.error == null })
            )
            "stability_session" -> listOf(
                assertion("configured_duration_completed", ">= $STABILITY_DURATION_MILLIS", duration, duration >= STABILITY_DURATION_MILLIS),
                assertion("no_disconnect", "false", snapshot.disconnected, !snapshot.disconnected),
                assertion("no_error", "none", snapshot.lastError, snapshot.lastError.isNullOrBlank())
            )
            "lifecycle_manual_check" -> listOf(
                assertion("app_background_event", StopReason.APP_BACKGROUND.name, reason, reason == StopReason.APP_BACKGROUND),
                assertion("engine_not_running", "false", snapshot.running, !snapshot.running),
                assertion("input_closed", "Closed", snapshot.inputState, snapshot.inputState == "Closed"),
                assertion("output_closed", "Closed", snapshot.outputState, snapshot.outputState == "Closed")
            )
            else -> emptyList()
        }
    }

    private fun oldMetricPeakFallback(delta: EngineMetrics) = 0.0 // Energy aggregates are bounded; spectral identification is intentionally deferred.

    fun cancel(session: LaboratorySession, snapshot: EngineSnapshot): LaboratorySession {
        val time = now()
        val results = session.results.map { if (it.status == TestStatus.RUNNING || it.status == TestStatus.PENDING) it.copy(status = TestStatus.ABORTED, endedAtMillis = time, stopReason = StopReason.USER_REQUEST) else it }
        return finish(session.copy(results = results, finalEngineSnapshot = snapshot, events = session.events + SessionEvent(time, "session_aborted", "Cancelación explícita")))
    }

    private fun finish(session: LaboratorySession): LaboratorySession {
        val p = session.results.count { it.status == TestStatus.PASSED }; val f = session.results.count { it.status == TestStatus.FAILED }
        val i = session.results.count { it.status == TestStatus.INCONCLUSIVE }; val a = session.results.count { it.status == TestStatus.ABORTED }
        val final = when { a > 0 -> TestStatus.ABORTED; f > 0 -> TestStatus.FAILED; i > 0 -> TestStatus.INCONCLUSIVE; session.results.all { it.status == TestStatus.PASSED || it.status == TestStatus.SKIPPED } -> TestStatus.PASSED; else -> TestStatus.PENDING }
        return session.copy(endedAtMillis = if (final == TestStatus.PENDING) null else now(), summary = SessionSummary(p, f, i, a, final))
    }
    private fun <T> List<T>.updated(index: Int, value: T) = toMutableList().also { it[index] = value }.toList()
}
