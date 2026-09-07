package br.com.gds.order_tracking

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.order_tracking.screen.OrderTrackingScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OrderTrackingScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun orderTrackingScreen_rendersTitle() {
        composeTestRule.setContent { OrderTrackingScreen() }
        composeTestRule.onNodeWithText("Módulo de Acompanhamento de Pedido").assertIsDisplayed()
    }
}
