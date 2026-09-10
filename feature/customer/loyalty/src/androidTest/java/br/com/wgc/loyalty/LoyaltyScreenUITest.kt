package br.com.wgc.loyalty

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.loyalty.screen.LoyaltyScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoyaltyScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun loyaltyScreen_rendersTitle() {
        composeTestRule.setContent { LoyaltyScreen() }
        composeTestRule.onNodeWithText("Módulo de Cartão Fidelidade e Cashback").assertIsDisplayed()
    }
}
