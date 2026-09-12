package br.com.wgc.maps.tracking

import androidx.lifecycle.viewModelScope
import br.com.wgc.ds_templates.screens.map.BaseRealtimeLocationViewModel
import br.com.wgc.ds_templates.screens.map.RealtimeLocationUiState
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
 * ViewModel corporativo para rastreamento de parceiros e entregas em tempo real.
 *
 * Integra com [GeolocationRepository] do OmniBackend consumindo coordenadas GPS
 * via WebSocket/Firestore Streams e atualizando o template de mapa em tempo real.
 *
 * @param geolocationRepository Repositório de telemetria e posições geográficas em tempo real.
 */
@HiltViewModel
class WgcLiveTrackingViewModel @Inject constructor(
    private val geolocationRepository: GeolocationRepository
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
        _uiState.update {
            it.copy(
                driverName = driverName,
                destinationAddress = destinationAddress,
                statusText = "Localizando parceiro...",
                isTrackingActive = true
            )
        }

        viewModelScope.launch {
            geolocationRepository.trackLocation(entityType, entityId).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        val location = result.data
                        _uiState.update {
                            it.copy(
                                statusText = "Posição atualizada (${location.latitude}, ${location.longitude})",
                                isTrackingActive = true
                            )
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