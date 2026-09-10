package br.com.wgc.whatsapp_direct

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.whatsapp_direct.screen.WhatsappDirectScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhatsappDirectScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun whatsappDirectScreen_rendersTitle() {
        composeTestRule.setContent { WhatsappDirectScreen() }
        composeTestRule.onNodeWithText("Módulo de Envio Direto para WhatsApp").assertIsDisplayed()
    }
}
