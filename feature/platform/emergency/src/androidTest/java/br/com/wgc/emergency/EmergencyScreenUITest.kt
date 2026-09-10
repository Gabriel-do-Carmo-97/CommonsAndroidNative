package br.com.wgc.emergency

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.emergency.screen.EmergencyScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmergencyScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun emergencyScreen_rendersTitle() {
        composeTestRule.setContent { EmergencyScreen() }
        composeTestRule.onNodeWithText("Módulo de Botão de Pânico WGC").assertIsDisplayed()
    }
}
