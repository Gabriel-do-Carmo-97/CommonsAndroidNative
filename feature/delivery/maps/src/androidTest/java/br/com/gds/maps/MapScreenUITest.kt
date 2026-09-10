package br.com.gds.maps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.maps.screen.MapScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mapScreen_shouldRenderTitleCorrectly() {
        composeTestRule.setContent {
            MapScreen()
        }

        composeTestRule.onNodeWithText("Módulo de Mapas e Geolocalização").assertIsDisplayed()
    }
}
