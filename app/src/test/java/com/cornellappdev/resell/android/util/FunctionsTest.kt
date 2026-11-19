package com.cornellappdev.resell.android.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FunctionsTest {

    @Test
    fun testGetRelativeTimeSpanWithAlreadyFormattedString() {
        val formatted = "53 minutes ago"
        val result = getRelativeTimeSpan(formatted)
        assertEquals("53 minutes ago", result)
    }

    @Test
    fun testGetRelativeTimeSpanWithBlank() {
        assertEquals("", getRelativeTimeSpan(""))
    }
}
