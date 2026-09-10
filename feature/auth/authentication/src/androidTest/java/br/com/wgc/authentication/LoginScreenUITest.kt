package br.com.wgc.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.authentication.login.LoginWGCScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_shouldRenderElementsCorrectly() {
        composeTestRule.setContent {
            LoginWGCScreen()
        }

        composeTestRule.onNodeWithText("Login", substring = true).assertIsDisplayed()
    }
}
