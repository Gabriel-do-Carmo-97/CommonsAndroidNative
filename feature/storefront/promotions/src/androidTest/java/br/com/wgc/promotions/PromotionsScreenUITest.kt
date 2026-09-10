package br.com.wgc.promotions

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.promotions.screen.PromotionsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PromotionsScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun promotionsScreen_rendersTitle() {
        composeTestRule.setContent { PromotionsScreen() }
        composeTestRule.onNodeWithText("Módulo de Promocões e Cupons").assertIsDisplayed()
    }
}
