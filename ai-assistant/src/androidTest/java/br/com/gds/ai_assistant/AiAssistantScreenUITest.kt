package br.com.gds.ai_assistant

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.ai_assistant.screen.AiAssistantScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AiAssistantScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun aiAssistantScreen_rendersTitle() {
        composeTestRule.setContent { AiAssistantScreen() }
        composeTestRule.onNodeWithText("Módulo de Assistente de IA WGC").assertIsDisplayed()
    }
}
