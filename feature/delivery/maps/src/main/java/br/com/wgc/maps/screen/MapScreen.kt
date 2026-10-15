package br.com.wgc.maps.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.wgc.maps.tracking.WgcLiveTrackingScreen

/**
 * Tela corporativa de Mapas e Acompanhamento de Rotas de Entrega.
 *
 * Provê visualização em tempo real da posição geográfica de condutores e frotas,
 * delegando diretamente para a [WgcLiveTrackingScreen] integrada ao OmniBackend e Design System.
 *
 * ### 📌 Quando Usar:
 * Renderize esta tela ao navegar para a visualização de mapa durante uma entrega em andamento
 * ou ao abrir a rota detalhada do entregador.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDeRastreioAoVivo() {
 *     MapScreen(
 *         entityId = "driver_448",
 *         driverName = "Carlos Silva (Moto)",
 *         destinationAddress = "Rua Augusta, 500 - SP"
 *     )
 * }
 * ```
 *
 * @param modifier Modificador de layout Jetpack Compose aplicado à raiz da tela.
 * @param entityId Identificador único do entregador ou veículo parceiro rastreado.
 * @param entityType Tipo de coleção ou entidade registrada (padrão: "drivers").
 * @param driverName Nome de exibição do motorista para o usuário.
 * @param destinationAddress Endereço formatado do ponto de entrega.
 * @param onContactDriver Ação acionada quando o usuário toca no botão de contato direto com o motorista.
 */
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    entityId: String = "driver_demo_01",
    entityType: String = "drivers",
    driverName: String = "Motorista Parceiro",
    destinationAddress: String = "Destino Selecionado",
    onContactDriver: () -> Unit = {}
) {
    WgcLiveTrackingScreen(
        entityId = entityId,
        modifier = modifier,
        entityType = entityType,
        driverName = driverName,
        destinationAddress = destinationAddress,
        onContactDriver = onContactDriver
    )
}
