package br.com.wgc.telemetry.screen

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
 * Tela de monitoramento de telemetria veicular, status de sensores e tracking em tempo real.
 *
 * @param modifier Modificador de layout Compose.
 * @param title TÃ­tulo do painel de telemetria.
 */
@Composable
fun TelemetryScreen(
    modifier: Modifier = Modifier,
    title: String = "MÃ³dulo de Telemetria e GPS WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Rastreamento GPS em background e telemetria de alta precisÃ£o WGC", style = MaterialTheme.typography.bodyMedium)
    }
}