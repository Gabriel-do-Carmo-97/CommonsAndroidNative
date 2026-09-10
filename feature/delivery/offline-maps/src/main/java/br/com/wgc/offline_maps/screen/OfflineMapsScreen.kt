package br.com.wgc.offline_maps.screen

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
 * Tela do mÃ³dulo de Mapas Offline e Cache Local de Rotas.
 *
 * Exibe regiÃµes baixadas para visualizaÃ§Ã£o sem conectividade e status
 * do armazenamento interno de cartografia vetorial.
 *
 * @param modifier Modificador Compose aplicado na raiz do contÃªiner.
 * @param title TÃ­tulo customizado exibido no topo.
 */
@Composable
fun OfflineMapsScreen(
    modifier: Modifier = Modifier,
    title: String = "MÃ³dulo de Mapas Offline WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "NavegaÃ§Ã£o turn-by-turn e cache de mapas locais WGC", style = MaterialTheme.typography.bodyMedium)
    }
}