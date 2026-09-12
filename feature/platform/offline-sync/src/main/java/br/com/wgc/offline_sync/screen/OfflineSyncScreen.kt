package br.com.wgc.offline_sync.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Tela de monitoramento de integridade e status de sincronização offline-first.
 *
 * @param modifier Modificador de layout Compose.
 * @param title Título do painel de sincronização.
 */
@Composable
fun OfflineSyncScreen(
    modifier: Modifier = Modifier,
    title: String = "Módulo de Sincronização Offline WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Operação offline-first e sincronização automática WGC", style = MaterialTheme.typography.bodyMedium)
    }
}