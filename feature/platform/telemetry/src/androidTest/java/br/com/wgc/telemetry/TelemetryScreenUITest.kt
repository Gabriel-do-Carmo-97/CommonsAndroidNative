package br.com.wgc.telemetry

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.telemetry.screen.TelemetryScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TelemetryScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun telemetryScreen_rendersTitle() {
        composeTestRule.setContent { TelemetryScreen() }
        composeTestRule.onNodeWithText("Módulo de Telemetria e GPS WGC").assertIsDisplayed()
    }
}
