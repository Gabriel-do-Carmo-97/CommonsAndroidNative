package br.com.gds.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.settings.screen.SettingsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun settingsScreen_rendersTitle() {
        composeTestRule.setContent { SettingsScreen() }
        composeTestRule.onNodeWithText("Módulo de Configurações e Temas").assertIsDisplayed()
    }
}
