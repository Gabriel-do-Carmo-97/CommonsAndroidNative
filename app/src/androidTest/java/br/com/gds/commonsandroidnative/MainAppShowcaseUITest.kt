package br.com.gds.commonsandroidnative

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainAppShowcaseUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showcase_navigationTabs_shouldNavigateBetweenModules() {
        // Assert initial tab (Auth)
        composeTestRule.onNodeWithText("Auth").assertIsDisplayed()

        // Click on Maps tab
        composeTestRule.onNodeWithText("Maps").performClick()
        composeTestRule.onNodeWithText("Módulo de Mapas e Geolocalização").assertIsDisplayed()

        // Click on Message tab
        composeTestRule.onNodeWithText("Message").performClick()
        composeTestRule.onNodeWithText("Módulo de Mensageria e Chat").assertIsDisplayed()

        // Click on Payment tab
        composeTestRule.onNodeWithText("Payment").performClick()
        composeTestRule.onNodeWithText("Módulo de Pagamentos e Checkout").assertIsDisplayed()
    }
}
