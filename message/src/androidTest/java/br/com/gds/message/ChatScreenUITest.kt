package br.com.gds.message

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.message.screen.ChatScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun chatScreen_shouldRenderTitleCorrectly() {
        composeTestRule.setContent {
            ChatScreen()
        }

        composeTestRule.onNodeWithText("Módulo de Mensageria e Chat").assertIsDisplayed()
    }
}
