package br.com.gds.analytics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.analytics.screen.AnalyticsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnalyticsScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun analyticsScreen_rendersTitle() {
        composeTestRule.setContent { AnalyticsScreen() }
        composeTestRule.onNodeWithText("Módulo de Analytics e Dashboard WGC").assertIsDisplayed()
    }
}
