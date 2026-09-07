package br.com.gds.multi_language

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.multi_language.screen.MultiLanguageScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiLanguageScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun multiLanguageScreen_rendersTitle() {
        composeTestRule.setContent { MultiLanguageScreen() }
        composeTestRule.onNodeWithText("Módulo de Internacionalização WGC").assertIsDisplayed()
    }
}
