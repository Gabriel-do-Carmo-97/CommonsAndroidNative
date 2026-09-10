package br.com.wgc.quotation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.quotation.screen.QuotationScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuotationScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun quotationScreen_rendersTitle() {
        composeTestRule.setContent { QuotationScreen() }
        composeTestRule.onNodeWithText("Módulo de Orçamentos e Serviços").assertIsDisplayed()
    }
}
