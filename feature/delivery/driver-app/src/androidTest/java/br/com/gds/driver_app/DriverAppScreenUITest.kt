package br.com.gds.driver_app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.driver_app.screen.DriverAppScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DriverAppScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun driverAppScreen_rendersTitle() {
        composeTestRule.setContent { DriverAppScreen() }
        composeTestRule.onNodeWithText("Módulo do Entregador e Logística WGC").assertIsDisplayed()
    }
}
