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
 * Tela de configuraÃ§Ã£o e monitoramento de Cercas Virtuais (Geofencing).
 *
 * Permite delimitaÃ§Ã£o de perÃ­metros geogrÃ¡ficos circulares ou poligonais
 * e acionamento de eventos automÃ¡ticos de entrada e saÃ­da.
 *
 * @param modifier Modificador de layout Compose aplicado na raiz.
 * @param title TÃ­tulo do painel de cercamento virtual.
 */
@Composable
fun GeofencingScreen(
    modifier: Modifier = Modifier,
    title: String = "MÃ³dulo de Cercas Virtuais WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Cercas virtuais automÃ¡ticas e alertas de proximidade WGC", style = MaterialTheme.typography.bodyMedium)
    }
}