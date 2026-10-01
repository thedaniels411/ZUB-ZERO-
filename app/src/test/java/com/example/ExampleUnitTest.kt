package com.example

import com.example.ui.viewmodel.AppNavTab
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun verifyMainNavigationDestinations() {
        // Verify the 4 primary destinations exist and have appropriate labels
        assertEquals("Video Generator", AppNavTab.AI_STUDIO.title)
        assertEquals("Marketplace", AppNavTab.MARKETPLACE.title)
        assertEquals("Hotel Locator", AppNavTab.HOTELS.title)
        assertEquals("Style Storefront", AppNavTab.FASHION.title)

        // Verify all 4 are distinct
        val tabs = listOf(
            AppNavTab.AI_STUDIO,
            AppNavTab.MARKETPLACE,
            AppNavTab.HOTELS,
            AppNavTab.FASHION
        )
        assertEquals(4, tabs.distinct().size)
    }
}

