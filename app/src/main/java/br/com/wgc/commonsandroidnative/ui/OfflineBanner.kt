package br.com.wgc.commonsandroidnative.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Componente visual de notificação que alerta o usuário sobre a ausência de conectividade de rede.
 *
 * Apresenta animação suave de entrada e saída no topo da hierarquia visual, informando que o aplicativo
 * está funcionando com dados em cache local (Modo Offline).
 *
 * ### 📌 Quando Usar:
 * Insira logo abaixo da TopAppBar ou no topo do Scaffold principal de telas que dependem de rede,
 * sincronização em tempo real ou consultas ao OmniBackend.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun MinhaTela(networkMonitor: NetworkMonitor) {
 *     val isConnected by networkMonitor.isConnected().collectAsState(initial = true)
 *
 *     Column {
 *         OfflineBanner(isOffline = !isConnected)
 *         // Conteúdo da tela...
 *     }
 * }
 * ```
 *
 * @param isOffline Indica se o dispositivo está desconectado da internet.
 * @param modifier Modificador de layout Jetpack Compose.
 */
@Composable
fun OfflineBanner(
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOffline,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "Ícone de sem conexão",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Sem conexão com a internet • Operando em Modo Offline",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
