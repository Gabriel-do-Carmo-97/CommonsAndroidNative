package br.com.wgc.maps.tracking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.design_system.templates.screens.map.RealtimeLocationMapScreenTemplate

/**
 * Tela corporativa para acompanhamento de motoristas e entregas com rastreamento GPS ao vivo.
 *
 * Integra templates oficiais do Design System ([RealtimeLocationMapScreenTemplate])
 * com a infraestrutura reativa de geolocalização do OmniBackend.
 *
 * ### 📌 Quando Usar:
 * Renderize esta tela na visualização detalhada do mapa de entrega, permitindo ao consumidor
 * acompanhar o deslocamento do veículo parceiro em tempo real com indicador de rota e status.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDeRastreamentoAoVivo() {
 *     WgcLiveTrackingScreen(
 *         entityId = "driver_789",
 *         driverName = "Roberto Santos (Carro)",
 *         destinationAddress = "Av. Brasil, 1200",
 *         onContactDriver = { /* Iniciar chamada telefônica ou abrir chat */ }
 *     )
 * }
 * ```
 *
 * @param entityId Identificador único da entidade/motorista monitorado no OmniBackend.
 * @param modifier Modificador de layout Compose para estilização externa.
 * @param entityType Tipo da coleção de geolocalização (padrão: "drivers").
 * @param driverName Nome de exibição do condutor para a interface.
 * @param destinationAddress Endereço formatado do ponto de desembarque/entrega.
 * @param onContactDriver Callback disparado ao acionar o botão de contato com o motorista.
 * @param viewModel ViewModel injetado com escuta reativa das coordenadas GPS.
 */
@Composable
fun WgcLiveTrackingScreen(
    entityId: String,
    modifier: Modifier = Modifier,
    entityType: String = "drivers",
    driverName: String = "Motorista Parceiro",
    destinationAddress: String = "Destino Selecionado",
    onContactDriver: () -> Unit = {},
    viewModel: WgcLiveTrackingViewModel = hiltViewModel()
) {
    LaunchedEffect(entityId) {
        viewModel.initializeTracking(
            entityType = entityType,
            entityId = entityId,
            driverName = driverName,
            destinationAddress = destinationAddress,
            onContact = onContactDriver
        )
    }

    RealtimeLocationMapScreenTemplate(viewModel = viewModel)
}
