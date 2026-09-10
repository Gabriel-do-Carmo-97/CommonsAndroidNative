package br.com.wgc.cart

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.cart.screen.CartScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun cartScreen_rendersTitle() {
        composeTestRule.setContent { CartScreen() }
        composeTestRule.onNodeWithText("Módulo de Carrinho e Taxas de Entrega").assertIsDisplayed()
    }
}
