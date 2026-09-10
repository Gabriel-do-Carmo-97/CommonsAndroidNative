package br.com.gds.scheduling

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.scheduling.screen.SchedulingScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SchedulingScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun schedulingScreen_rendersTitle() {
        composeTestRule.setContent { SchedulingScreen() }
        composeTestRule.onNodeWithText("Módulo de Agendamento de Serviços").assertIsDisplayed()
    }
}
