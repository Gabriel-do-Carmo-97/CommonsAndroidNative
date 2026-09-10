package br.com.wgc.force_update.screen

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

/**
 * Estado da interface de verificaÃ§Ã£o de versÃ£o e atualizaÃ§Ã£o forÃ§ada.
 *
 * @property title TÃ­tulo do mÃ³dulo de manutenÃ§Ã£o.
 * @property isUpdateRequired Indica se existe nova versÃ£o recomendada ou obrigatÃ³ria.
 * @property isForceUpdate Indica se a versÃ£o instalada estÃ¡ bloqueada para uso.
 * @property currentVersionCode CÃ³digo de versÃ£o localmente instalado.
 * @property minRequiredVersionCode CÃ³digo mÃ­nimo da versÃ£o exigida pelo servidor.
 * @property storeUrl URL de redirecionamento para download na loja oficial.
 * @property isLoading Indica se a checagem remota estÃ¡ em andamento.
 * @property errorMessage Mensagem de erro caso a consulta ao Remote Config falhe.
 */
data class ForceUpdateUiState(
    val title: String = "MÃ³dulo de Force Update e ManutenÃ§Ã£o",
    val isUpdateRequired: Boolean = false,
    val isForceUpdate: Boolean = false,
    val currentVersionCode: Long = 1L,
    val minRequiredVersionCode: Long = 1L,
    val storeUrl: String = "https://play.google.com/store/apps",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel que consulta parÃ¢metros do [RemoteConfigRepository] para garantir conformidade de versÃ£o.
 *
 * @param remoteConfigRepository RepositÃ³rio do OmniBackend para consulta a configuraÃ§Ãµes dinÃ¢micas.
 */
@HiltViewModel
class ForceUpdateViewModel @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForceUpdateUiState())

    /** Fluxo observÃ¡vel do estado de atualizaÃ§Ã£o do app. */
    val uiState: StateFlow<ForceUpdateUiState> = _uiState.asStateFlow()

    /**
     * Consulta os parÃ¢metros remotos de versÃ£o mÃ­nima e ativaÃ§Ã£o de bloqueio.
     *
     * @param currentVersionCode VersÃ£o numÃ©rica localmente compilada do app.
     */
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
                            errorMessage = fetchResult.error.message ?: "Erro ao checar versÃ£o remota"
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