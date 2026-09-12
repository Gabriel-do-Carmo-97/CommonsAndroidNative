package br.com.wgc.emergency.screen

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
 * Tela do Botão de Pânico e Alerta Silencioso de Emergência com geolocalização imediata.
 *
 * @param modifier Modificador de layout Compose.
 * @param title Título do módulo de emergência.
 */
@Composable
fun EmergencyScreen(
    modifier: Modifier = Modifier,
    title: String = "Módulo de Botão de Pânico WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Botão de pânico e alerta silencioso de segurança WGC", style = MaterialTheme.typography.bodyMedium)
    }
}