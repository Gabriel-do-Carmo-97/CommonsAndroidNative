package br.com.gds.commonsandroidnative

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.gds.authentication.navigation.AuthNavigation
import br.com.gds.commonsandroidnative.ui.theme.CommonsAndroidNativeTheme
import br.com.gds.maps.screen.MapScreen
import br.com.gds.message.screen.ChatScreen
import br.com.gds.payment.screen.PaymentScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommonsAndroidNativeTheme {
                MainAppShowcaseScreen(
                    onAuthSuccess = {
                        Toast.makeText(this, "Autenticação realizada com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun MainAppShowcaseScreen(
    onAuthSuccess: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val items = listOf("Auth", "Maps", "Message", "Payment")

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(item) },
                        icon = { Text(item.take(1)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (selectedTab) {
            0 -> AuthNavigation(modifier = modifier, authSuccess = onAuthSuccess)
            1 -> MapScreen(modifier = modifier)
            2 -> ChatScreen(modifier = modifier)
            3 -> PaymentScreen(modifier = modifier)
        }
    }
}
