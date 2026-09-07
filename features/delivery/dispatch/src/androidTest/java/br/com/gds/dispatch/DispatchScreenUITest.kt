package br.com.gds.dispatch

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.dispatch.screen.DispatchScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DispatchScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun dispatchScreen_rendersTitle() {
        composeTestRule.setContent { DispatchScreen() }
        composeTestRule.onNodeWithText("Módulo de Dispatch e Alocação WGC").assertIsDisplayed()
    }
}
