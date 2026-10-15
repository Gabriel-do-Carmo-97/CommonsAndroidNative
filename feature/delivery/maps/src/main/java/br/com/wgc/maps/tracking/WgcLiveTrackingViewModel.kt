package br.com.wgc.maps.tracking

import androidx.lifecycle.viewModelScope
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.design_system.templates.screens.map.BaseRealtimeLocationViewModel
import br.com.wgc.design_system.templates.screens.map.RealtimeLocationUiState
import br.wgc.omnibackend.core.repository.realtime.GeolocationRepository
import br.wgc.omnibackend.core.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel corporativo para rastreamento de parceiros e entregas em tempo real com auditoria anti-fraude.
 *
 * Integra com o [GeolocationRepository] do OmniBackend consumindo coordenadas GPS
 * emitidas continuamente via Firestore Streams ou WebSocket, auditando anomalias via [MockLocationDetector]
 * e suavizando oscilações estocásticas pelo [br.com.wgc.core.location.geofence.KalmanLocationFilter].
 *
 * ### 📌 Quando Usar:
 * Utilize para telas de rastreamento com mapa vivo onde a latitude e longitude do entregador
 * precisam ser atualizadas instantaneamente com proteção anti-spoofing e alta precisão.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDeRastreamento(viewModel: WgcLiveTrackingViewModel = hiltViewModel()) {
 *     LaunchedEffect(Unit) {
 *         viewModel.initializeTracking(entityId = "driver_99", driverName = "João")
 *     }
 * }
 * ```
 *
 * @param geolocationRepository Repositório de telemetria e posições geográficas em tempo real do OmniBackend.
 * @param dispatchers Provedor unificado de CoroutineDispatchers do CoreAndroidNative.
 * @param mockLocationDetector Validador anti-fraude de coordenadas e filtro de Kalman.
 */
@HiltViewModel
class WgcLiveTrackingViewModel @Inject constructor(
    private val geolocationRepository: GeolocationRepository,
    private val dispatchers: CoroutineDispatchers,
    private val mockLocationDetector: MockLocationDetector = MockLocationDetector()
) : BaseRealtimeLocationViewModel() {

    private val _uiState = MutableStateFlow(RealtimeLocationUiState())

    /**
     * Fluxo reativo do estado da posição do entregador no mapa.
     */
    override val uiState: StateFlow<RealtimeLocationUiState> = _uiState.asStateFlow()

    private var onContactCallback: (() -> Unit)? = null

    /**
     * Inicializa a escuta ativa de coordenadas geográficas de uma entidade conectada.
     *
     * ### 📌 Quando Usar:
     * Invocado no início do ciclo de vida da tela de rastreamento ao vivo para conectar ao stream do condutor.
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * viewModel.initializeTracking(
     *     entityType = "drivers",
     *     entityId = "driver_123",
     *     driverName = "Marcos Souza",
     *     destinationAddress = "Rua das Flores, 120"
     * )
     * ```
     *
     * @param entityType Tipo de entidade rastreada (ex: `drivers`).
     * @param entityId Identificador único do entregador ou veículo parceiro.
     * @param driverName Nome de exibição do motorista para o cliente final.
     * @param destinationAddress Endereço formatado do ponto de entrega.
     * @param onContact Ação invocada quando o usuário clica no botão de ligar/contatar motorista.
     */
    fun initializeTracking(
        entityType: String = "drivers",
        entityId: String,
        driverName: String = "Motorista Parceiro",
        destinationAddress: String = "Destino Selecionado",
        onContact: () -> Unit = {}
    ) {
        this.onContactCallback = onContact
        mockLocationDetector.reset()
        _uiState.update {
            it.copy(
                driverName = driverName,
                destinationAddress = destinationAddress,
                statusText = "Localizando parceiro...",
                isTrackingActive = true
            )
        }

        viewModelScope.launch(dispatchers.main) {
            geolocationRepository.trackLocation(entityType, entityId).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        val location = result.data
                        val audit = mockLocationDetector.auditAndFilter(location)
                        if (audit.isValid) {
                            _uiState.update {
                                it.copy(
                                    statusText = "Posição atualizada (${audit.filteredLatitude}, ${audit.filteredLongitude})",
                                    isTrackingActive = true
                                )
                            }
                        } else if (audit.isMockDetected) {
                            _uiState.update {
                                it.copy(
                                    statusText = "Alerta de Segurança: Localização simulada detectada",
                                    isTrackingActive = false
                                )
                            }
                        }
                    }
                    is DataResult.Failure -> {
                        _uiState.update {
                            it.copy(
                                statusText = "Aguardando sinal de GPS...",
                                isTrackingActive = false
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Aciona o callback registrado para contato direto telefônico ou via chat com o motorista.
     */
    override fun onContactDriverClick() {
        onContactCallback?.invoke()
    }
}