package br.com.gds.biometric

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.biometric.screen.BiometricScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BiometricScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun biometricScreen_rendersTitle() {
        composeTestRule.setContent { BiometricScreen() }
        composeTestRule.onNodeWithText("Módulo de Biometria, PIN e App Lock").assertIsDisplayed()
    }
}
