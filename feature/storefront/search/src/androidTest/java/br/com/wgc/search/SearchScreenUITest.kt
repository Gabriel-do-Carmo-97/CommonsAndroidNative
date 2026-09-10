package br.com.wgc.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.search.screen.SearchScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun searchScreen_rendersTitle() {
        composeTestRule.setContent { SearchScreen() }
        composeTestRule.onNodeWithText("Módulo de Busca Reativa e Filtros").assertIsDisplayed()
    }
}
