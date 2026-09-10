package br.com.wgc.catalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.catalog.screen.CatalogScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun catalogScreen_rendersTitle() {
        composeTestRule.setContent { CatalogScreen() }
        composeTestRule.onNodeWithText("Módulo de Catálogo e Cardápio Digital").assertIsDisplayed()
    }
}
