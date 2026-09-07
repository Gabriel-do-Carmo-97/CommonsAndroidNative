package br.com.gds.commonsandroidnative.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class BackendProvider(val label: String) {
    FIREBASE("Firebase Firestore & Realtime"),
    SUPABASE("Supabase PostgREST"),
    REST_API("REST API Padrão WGC"),
    MOCK_IN_MEMORY("Mock / In-Memory Sandbox")
}

enum class BrandTheme(val label: String, val primaryColor: Color) {
    WGC_DEFAULT("WGC Default", Color(0xFF6200EE)),
    IFOOD("iFood (Vermelho)", Color(0xFFEA1D2C)),
    MERCADO_LIVRE("Mercado Livre (Amarelo)", Color(0xFFFFE600)),
    FINTECH_PURPLE("Fintech (Roxo Neon)", Color(0xFF8A05BE))
}

enum class NetworkCondition(val label: String) {
    ONLINE("100% Online (5G/Wi-Fi)"),
    OFFLINE("Offline (Simular Modo Avião)"),
    SLOW_3G("Conexão Instável (3G Lenta)")
}

enum class MockUserProfile(val label: String, val role: String) {
    CLIENT("Gabriel Carmo", "Cliente Final"),
    DRIVER("Lucas Silva", "Entregador Parceiro"),
    STORE_ADMIN("Admin Central", "Gestor da Loja"),
    GUEST("Visitante", "Não Autenticado")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperSandboxSheet(
    onDismissRequest: () -> Unit,
    currentProvider: BackendProvider,
    onSelectProvider: (BackendProvider) -> Unit,
    currentTheme: BrandTheme,
    onSelectTheme: (BrandTheme) -> Unit,
    currentNetwork: NetworkCondition,
    onSelectNetwork: (NetworkCondition) -> Unit,
    currentUser: MockUserProfile,
    onSelectUser: (MockUserProfile) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Developer Debug Sandbox",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Aceleração de desenvolvimento & Homologação",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Backend Provider Switcher
            SectionHeader(icon = Icons.Default.Cloud, title = "Backend Provider")
            Spacer(modifier = Modifier.height(8.dp))
            BackendProvider.entries.forEach { provider ->
                FilterChip(
                    selected = currentProvider == provider,
                    onClick = { onSelectProvider(provider) },
                    label = { Text(provider.label) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Brand Theme Switcher
            SectionHeader(icon = Icons.Default.Palette, title = "Design System Brand Theme")
            Spacer(modifier = Modifier.height(8.dp))
            BrandTheme.entries.forEach { theme ->
                FilterChip(
                    selected = currentTheme == theme,
                    onClick = { onSelectTheme(theme) },
                    label = { Text(theme.label) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Network Condition
            SectionHeader(icon = Icons.Default.NetworkCheck, title = "Simulador de Rede")
            Spacer(modifier = Modifier.height(8.dp))
            NetworkCondition.entries.forEach { network ->
                FilterChip(
                    selected = currentNetwork == network,
                    onClick = { onSelectNetwork(network) },
                    label = { Text(network.label) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Mock User Profile
            SectionHeader(icon = Icons.Default.AccountCircle, title = "Mock User Profile")
            Spacer(modifier = Modifier.height(8.dp))
            MockUserProfile.entries.forEach { user ->
                FilterChip(
                    selected = currentUser == user,
                    onClick = { onSelectUser(user) },
                    label = { Text("${user.label} (${user.role})") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}
