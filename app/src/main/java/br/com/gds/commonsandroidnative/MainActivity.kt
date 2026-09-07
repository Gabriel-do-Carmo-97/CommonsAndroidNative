package br.com.gds.commonsandroidnative

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import br.com.gds.authentication.config.AuthConfig
import br.com.gds.authentication.navigation.AuthNavigation
import br.com.gds.authentication.session.AuthSessionState
import br.com.gds.authentication.session.WgcAuthManager
import br.com.gds.commonsandroidnative.ui.theme.CommonsAndroidNativeTheme
import br.com.gds.maps.tracking.WgcLiveTrackingScreen
import br.com.gds.message.chat.WgcChatScreen
import br.com.gds.payment.screen.PaymentScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authManager: WgcAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommonsAndroidNativeTheme {
                MainAppShowcaseScreen(
                    authManager = authManager,
                    onAuthSuccess = {
                        Toast.makeText(this, "Autenticação realizada com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

sealed class ShowcaseTab(val title: String, val icon: ImageVector) {
    data object Auth : ShowcaseTab("Auth", Icons.Default.Lock)
    data object Maps : ShowcaseTab("Rastreio", Icons.Default.LocationOn)
    data object Chat : ShowcaseTab("Chat", Icons.Default.Chat)
    data object Payment : ShowcaseTab("Pagamento", Icons.Default.CreditCard)
}

@Composable
fun MainAppShowcaseScreen(
    authManager: WgcAuthManager,
    onAuthSuccess: () -> Unit
) {
    val tabs = listOf(
        ShowcaseTab.Auth,
        ShowcaseTab.Maps,
        ShowcaseTab.Chat,
        ShowcaseTab.Payment
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val sessionState by authManager.sessionState.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        label = { Text(tab.title) },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (tabs[selectedTabIndex]) {
            is ShowcaseTab.Auth -> {
                Column(modifier = modifier) {
                    when (val session = sessionState) {
                        is AuthSessionState.Authenticated -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Sessão Ativa",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = session.user.email ?: "Usuário: ${session.user.uid}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                authManager.logout()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Logout,
                                            contentDescription = "Desconectar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            // Usuário não autenticado: exibe o fluxo completo de autenticação
                        }
                    }

                    AuthNavigation(
                        modifier = Modifier.fillMaxSize(),
                        config = AuthConfig(
                            enableBiometrics = true,
                            enableSocialLogin = true,
                            enableAddressRegistration = true,
                            enableCarRegistration = true,
                            enableForgotPassword = true
                        ),
                        authSuccess = onAuthSuccess
                    )
                }
            }
            is ShowcaseTab.Maps -> {
                WgcLiveTrackingScreen(
                    entityId = "parceiro_demo_001",
                    driverName = "Carlos Silva - Parceiro Omni",
                    destinationAddress = "Av. Paulista, 1000 - Bela Vista, SP",
                    modifier = modifier
                )
            }
            is ShowcaseTab.Chat -> {
                val currentUserId = authManager.currentUser?.uid ?: "demo_user"
                WgcChatScreen(
                    conversationId = "suporte_geral_001",
                    currentUserId = currentUserId,
                    recipientName = "Atendimento ao Cliente Omni",
                    modifier = modifier
                )
            }
            is ShowcaseTab.Payment -> {
                PaymentScreen(modifier = modifier)
            }
        }
    }
}
