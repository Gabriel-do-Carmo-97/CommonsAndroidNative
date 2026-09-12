package br.com.wgc.dispatch.screen

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
 * Tela do painel de despacho (Dispatch) e alocação de pedidos.
 *
 * Apresenta a interface de gestão e visualização da esteira operacional
 * de distribuição e roteirização para entregadores parceiros.
 *
 * @param modifier Modificador de layout Compose a ser aplicado na raiz.
 * @param title Título customizado exibido no topo do painel.
 */
@Composable
fun DispatchScreen(
    modifier: Modifier = Modifier,
    title: String = "Módulo de Dispatch e Alocação WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Motor inteligente de alocação de pedidos e roteirização WGC", style = MaterialTheme.typography.bodyMedium)
    }
}