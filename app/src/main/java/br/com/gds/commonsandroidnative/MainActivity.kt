package br.com.gds.commonsandroidnative

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.gds.authentication.config.AuthConfig
import br.com.gds.authentication.navigation.AuthNavigation
import br.com.gds.authentication.session.AuthSessionState
import br.com.gds.authentication.session.WgcAuthManager
import br.com.gds.biometric.screen.BiometricScreen
import br.com.gds.cart.screen.CartScreen
import br.com.gds.catalog.screen.CatalogScreen
import br.com.gds.commonsandroidnative.ui.theme.CommonsAndroidNativeTheme
import br.com.gds.feedback.screen.FeedbackScreen
import br.com.gds.force_update.screen.ForceUpdateScreen
import br.com.gds.loyalty.screen.LoyaltyScreen
import br.com.gds.maps.tracking.WgcLiveTrackingScreen
import br.com.gds.media_picker.screen.MediaPickerScreen
import br.com.gds.message.chat.WgcChatScreen
import br.com.gds.onboarding.screen.OnboardingScreen
import br.com.gds.order_tracking.screen.OrderTrackingScreen
import br.com.gds.payment.screen.PaymentScreen
import br.com.gds.profile.screen.ProfileScreen
import br.com.gds.promotions.screen.PromotionsScreen
import br.com.gds.quotation.screen.QuotationScreen
import br.com.gds.reviews_store.screen.ReviewsStoreScreen
import br.com.gds.scheduling.screen.SchedulingScreen
import br.com.gds.search.screen.SearchScreen
import br.com.gds.settings.screen.SettingsScreen
import br.com.gds.stores.screen.StoresScreen
import br.com.gds.whatsapp_direct.screen.WhatsappDirectScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShowcaseScreen(
    authManager: WgcAuthManager,
    onAuthSuccess: (String) -> Unit
) {
    var selectedDestinationRoute by rememberSaveable { mutableStateOf<String?>(null) }
    val sessionState by authManager.sessionState.collectAsState()
    val scope = rememberCoroutineScope()

    val currentDestination = ShowcaseDestination.allDestinations.firstOrNull { it.route == selectedDestinationRoute }

    BackHandler(enabled = currentDestination != null) {
        selectedDestinationRoute = null
    }

    if (currentDestination != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(currentDestination.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                currentDestination.category.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { selectedDestinationRoute = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar ao Showcase")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentDestination) {
                    is ShowcaseDestination.Catalog -> CatalogScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Cart -> CartScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Search -> SearchScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Promotions -> PromotionsScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Payment -> PaymentScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Stores -> StoresScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Scheduling -> SchedulingScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Quotation -> QuotationScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.OrderTracking -> OrderTrackingScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Maps -> WgcLiveTrackingScreen(
                        entityId = "driver_demo_01",
                        modifier = Modifier.fillMaxSize()
                    )
                    is ShowcaseDestination.Loyalty -> LoyaltyScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Feedback -> FeedbackScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.ReviewsStore -> ReviewsStoreScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.WhatsappDirect -> WhatsappDirectScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Chat -> {
                        val currentUserId = authManager.currentUser?.uid ?: "demo_user"
                        WgcChatScreen(
                            conversationId = "suporte_geral_001",
                            currentUserId = currentUserId,
                            recipientName = "Atendimento Omni WGC",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is ShowcaseDestination.Auth -> {
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
                    is ShowcaseDestination.Profile -> ProfileScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Settings -> SettingsScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Biometric -> BiometricScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.MediaPicker -> MediaPickerScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.ForceUpdate -> ForceUpdateScreen(modifier = Modifier.fillMaxSize())
                    is ShowcaseDestination.Onboarding -> OnboardingScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    } else {
        // Showcase Hub Principal
        ShowcaseHubHome(
            sessionState = sessionState,
            onLogout = {
                scope.launch { authManager.logout() }
            },
            onSelectDestination = { destination ->
                selectedDestinationRoute = destination.route
            }
        )
    }
}

@Composable
private fun ShowcaseHubHome(
    sessionState: AuthSessionState,
    onLogout: () -> Unit,
    onSelectDestination: (ShowcaseDestination) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<ShowcaseCategory?>(null) }

    val filteredDestinations = ShowcaseDestination.allDestinations.filter { dest ->
        val matchesCategory = selectedCategory == null || dest.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                dest.title.contains(searchQuery, ignoreCase = true) ||
                dest.description.contains(searchQuery, ignoreCase = true) ||
                dest.category.title.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Header
                Text(
                    text = "Commons Native Showcase",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Ecossistema Completo • 22 Módulos Reutilizáveis",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Session Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sessão WGC OmniBackend",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            val userText = when (sessionState) {
                                is AuthSessionState.Authenticated -> sessionState.user.email ?: "Usuário: ${sessionState.user.uid}"
                                else -> "Modo Visitante / Convidado"
                            }
                            Text(
                                text = userText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        if (sessionState is AuthSessionState.Authenticated) {
                            TextButton(onClick = onLogout) {
                                Text("Sair", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar módulo, template ou recurso...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Todos (${ShowcaseDestination.allDestinations.size})") }
                    )
                    ShowcaseCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = if (selectedCategory == category) null else category
                            },
                            label = { Text(category.title) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (filteredDestinations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum módulo encontrado para a busca.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredDestinations, key = { it.route }) { dest ->
                    DestinationCard(
                        destination = dest,
                        onClick = { onSelectDestination(dest) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DestinationCard(
    destination: ShowcaseDestination,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = destination.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = destination.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = destination.category.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Abrir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
