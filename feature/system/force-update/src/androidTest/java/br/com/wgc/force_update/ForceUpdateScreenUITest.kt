package br.com.wgc.force_update

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.force_update.screen.ForceUpdateScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForceUpdateScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun forceUpdateScreen_rendersTitle() {
        composeTestRule.setContent { ForceUpdateScreen() }
        composeTestRule.onNodeWithText("Módulo de Force Update e Manutenção").assertIsDisplayed()
    }
}
