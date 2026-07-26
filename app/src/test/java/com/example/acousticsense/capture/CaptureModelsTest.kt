package com.example.acousticsense.capture

import org.junit.Assert.assertEquals
import org.junit.Test

class CaptureModelsTest {
    @Test fun durationIsFormattedAsMinutesAndSeconds() {
        assertEquals("02:05", DurationFormatter.format(125_999))
    }

    @Test fun negativeDurationIsClamped() {
        assertEquals("00:00", DurationFormatter.format(-1))
    }

    @Test fun unprocessedSelectionIncludesSafeMicFallback() {
        assertEquals(
            AudioSourceChoice("UNPROCESSED", listOf(AudioSourceSelector.SOURCE_UNPROCESSED, AudioSourceSelector.SOURCE_MIC)),
            AudioSourceSelector.select(true)
        )
    }

    @Test fun micIsSelectedWhenUnprocessedIsNotDeclared() {
        assertEquals(listOf(AudioSourceSelector.SOURCE_MIC), AudioSourceSelector.select(false).candidates)
    }
}
