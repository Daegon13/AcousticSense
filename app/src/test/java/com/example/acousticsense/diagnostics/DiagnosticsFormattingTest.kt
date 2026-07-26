package com.example.acousticsense.diagnostics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DiagnosticsFormattingTest {
    @Test fun blankValuesAreUnknown() = assertEquals("unknown", "  ".orUnknown())
    @Test fun positiveIntegerPropertyIsParsed() = assertEquals(48000, "48000".asPositiveIntOrNull())
    @Test fun invalidPropertyIsUnavailable() = assertNull("not-a-number".asPositiveIntOrNull())
    @Test fun nonPositivePropertyIsUnavailable() = assertNull("0".asPositiveIntOrNull())
}
