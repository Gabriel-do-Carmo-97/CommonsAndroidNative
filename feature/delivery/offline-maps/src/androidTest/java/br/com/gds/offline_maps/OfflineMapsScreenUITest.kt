package br.com.gds.offline_maps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.offline_maps.screen.OfflineMapsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineMapsScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun offlineMapsScreen_rendersTitle() {
        composeTestRule.setContent { OfflineMapsScreen() }
        composeTestRule.onNodeWithText("Módulo de Mapas Offline WGC").assertIsDisplayed()
    }
}
