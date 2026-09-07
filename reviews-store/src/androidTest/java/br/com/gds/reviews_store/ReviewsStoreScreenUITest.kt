package br.com.gds.reviews_store

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.reviews_store.screen.ReviewsStoreScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewsStoreScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun reviewsStoreScreen_rendersTitle() {
        composeTestRule.setContent { ReviewsStoreScreen() }
        composeTestRule.onNodeWithText("Módulo de Avaliação da Loja e Pedidos").assertIsDisplayed()
    }
}
