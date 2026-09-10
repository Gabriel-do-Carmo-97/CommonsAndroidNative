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

@HiltViewModel
class WgcLiveTrackingViewModel @Inject constructor(
    private val geolocationRepository: GeolocationRepository
) : BaseRealtimeLocationViewModel() {

    private val _uiState = MutableStateFlow(RealtimeLocationUiState())
    override val uiState: StateFlow<RealtimeLocationUiState> = _uiState.asStateFlow()

    private var onContactCallback: (() -> Unit)? = null

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

    override fun onContactDriverClick() {
        onContactCallback?.invoke()
    }
}
