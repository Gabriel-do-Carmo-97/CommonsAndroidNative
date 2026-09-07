package br.com.gds.stores

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.stores.screen.StoresScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StoresScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun storesScreen_rendersTitle() {
        composeTestRule.setContent { StoresScreen() }
        composeTestRule.onNodeWithText("Módulo de Multi-Lojas e Filiais").assertIsDisplayed()
    }
}
