package br.com.wgc.geofencing.screen

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
 * Tela de configuração e monitoramento de Cercas Virtuais (Geofencing).
 *
 * Permite delimitação de perímetros geográficos circulares ou poligonais
 * e acionamento de eventos automáticos de entrada e saída.
 *
 * @param modifier Modificador de layout Compose aplicado na raiz.
 * @param title Título do painel de cercamento virtual.
 */
@Composable
fun GeofencingScreen(
    modifier: Modifier = Modifier,
    title: String = "Módulo de Cercas Virtuais WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Cercas virtuais automáticas e alertas de proximidade WGC", style = MaterialTheme.typography.bodyMedium)
    }
}