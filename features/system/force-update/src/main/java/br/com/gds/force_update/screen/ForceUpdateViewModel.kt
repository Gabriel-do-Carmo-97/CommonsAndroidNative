package br.com.gds.force_update.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.wgc.omnibackend.core.repository.RemoteConfigRepository
import br.wgc.omnibackend.core.utils.DataResult
import br.wgc.omnibackend.core.utils.message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForceUpdateUiState(
    val title: String = "Módulo de Force Update e Manutenção",
    val isUpdateRequired: Boolean = false,
    val isForceUpdate: Boolean = false,
    val currentVersionCode: Long = 1L,
    val minRequiredVersionCode: Long = 1L,
    val storeUrl: String = "https://play.google.com/store/apps",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ForceUpdateViewModel @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForceUpdateUiState())
    val uiState: StateFlow<ForceUpdateUiState> = _uiState.asStateFlow()

    fun checkForUpdate(currentVersionCode: Long = 1L) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentVersionCode = currentVersionCode) }
            when (val fetchResult = remoteConfigRepository.fetchAndActivate()) {
                is DataResult.Success -> {
                    val minVersionResult = remoteConfigRepository.getLong("min_required_app_version")
                    val isForceResult = remoteConfigRepository.getBoolean("force_update_enabled")
                    val storeUrlResult = remoteConfigRepository.getString("store_url")

                    val minVersion = if (minVersionResult is DataResult.Success) minVersionResult.data else 1L
                    val isForce = if (isForceResult is DataResult.Success) isForceResult.data else false
                    val storeUrl = if (storeUrlResult is DataResult.Success && storeUrlResult.data.isNotBlank()) {
                        storeUrlResult.data
                    } else {
                        "https://play.google.com/store/apps"
                    }

                    val updateRequired = currentVersionCode < minVersion

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isUpdateRequired = updateRequired,
                            isForceUpdate = updateRequired && isForce,
                            minRequiredVersionCode = minVersion,
                            storeUrl = storeUrl,
                            errorMessage = null
                        )
                    }
                }
                is DataResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = fetchResult.error.message ?: "Erro ao checar versão remota"
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}
