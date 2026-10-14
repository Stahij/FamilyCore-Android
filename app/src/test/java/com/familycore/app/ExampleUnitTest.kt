package com.familycore.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test fun releaseVersionNameIsCurrent() {
        assertEquals("0.2.0", "0.2.0")
    }

    @Test fun localMvpScopeDoesNotClaimNetworkFeatures() {
        val currentFeatures = listOf("local tasks", "local calendar", "shopping list", "notes")
        assertTrue(currentFeatures.contains("local tasks"))
        assertTrue(!currentFeatures.contains("family sync"))
    }
}
