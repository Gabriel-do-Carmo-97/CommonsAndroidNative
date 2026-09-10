package br.com.wgc.payment

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.payment.screen.PaymentScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaymentScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun paymentScreen_shouldRenderTitleCorrectly() {
        composeTestRule.setContent {
            PaymentScreen()
        }

        composeTestRule.onNodeWithText("Módulo de Pagamentos e Checkout").assertIsDisplayed()
    }
}
