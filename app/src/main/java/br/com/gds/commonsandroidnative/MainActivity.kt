package br.com.gds.commonsandroidnative

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import br.com.gds.authentication.config.AuthConfig
import br.com.gds.authentication.navigation.AuthNavigation
import br.com.gds.authentication.session.AuthSessionState
import br.com.gds.authentication.session.WgcAuthManager
import br.com.gds.catalog.screen.CatalogScreen
import br.com.gds.commonsandroidnative.ui.theme.CommonsAndroidNativeTheme
import br.com.gds.message.chat.WgcChatScreen
import br.com.gds.scheduling.screen.SchedulingScreen
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
                    onAuthSuccess = { email ->
                        Toast.makeText(this, "Autenticado com sucesso: $email", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

sealed class ShowcaseTab(val title: String) {
    data object Auth : ShowcaseTab("Auth & Core")
    data object Catalog : ShowcaseTab("Catálogo/Cart")
    data object Services : ShowcaseTab("Agendamentos")
    data object Chat : ShowcaseTab("Chat/Rastreio")
}

@Composable
fun MainAppShowcaseScreen(
    authManager: WgcAuthManager,
    onAuthSuccess: (String) -> Unit
) {
    val tabs = listOf(
        ShowcaseTab.Auth,
        ShowcaseTab.Catalog,
        ShowcaseTab.Services,
        ShowcaseTab.Chat
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
                        icon = { Text(tab.title.take(1)) }
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
                                            text = "Sessão Ativa WGC",
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
                                        Text("Sair", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                        else -> {
                            // Exibe fluxo completo de autenticação
                        }
                    }

                    AuthNavigation(
                        modifier = Modifier.fillMaxSize(),
                        config = AuthConfig(
                            enableBiometrics = true,
                            enableAddressRegistration = true,
                            enableCarRegistration = true,
                            isClient = true
                        ),
                        authSuccess = onAuthSuccess
                    )
                }
            }
            is ShowcaseTab.Catalog -> {
                CatalogScreen(modifier = modifier)
            }
            is ShowcaseTab.Services -> {
                SchedulingScreen(modifier = modifier)
            }
            is ShowcaseTab.Chat -> {
                val currentUserId = authManager.currentUser?.uid ?: "demo_user"
                WgcChatScreen(
                    conversationId = "suporte_geral_001",
                    currentUserId = currentUserId,
                    recipientName = "Atendimento ao Cliente Omni/WGC",
                    modifier = modifier
                )
            }
        }
    }
}
