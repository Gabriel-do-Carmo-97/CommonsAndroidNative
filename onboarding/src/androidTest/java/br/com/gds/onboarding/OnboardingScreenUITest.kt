package br.com.gds.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.onboarding.screen.OnboardingScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingScreen_shouldRenderTitleCorrectly() {
        composeTestRule.setContent {
            OnboardingScreen()
        }

        composeTestRule.onNodeWithText("Módulo de Onboarding e Permissões").assertIsDisplayed()
    }
}
