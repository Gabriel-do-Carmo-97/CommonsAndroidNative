package br.com.wgc.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.profile.screen.ProfileScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun profileScreen_rendersTitle() {
        composeTestRule.setContent { ProfileScreen() }
        composeTestRule.onNodeWithText("Módulo de Perfil e Privacidade LGPD").assertIsDisplayed()
    }
}
