package br.com.gds.geofencing

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.geofencing.screen.GeofencingScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeofencingScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun geofencingScreen_rendersTitle() {
        composeTestRule.setContent { GeofencingScreen() }
        composeTestRule.onNodeWithText("Módulo de Cercas Virtuais WGC").assertIsDisplayed()
    }
}
