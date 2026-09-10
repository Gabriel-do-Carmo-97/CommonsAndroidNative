package br.com.wgc.biometric.screen

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import br.com.wgc.core.security.biometric.BiometricAuthHelper
import br.com.wgc.core.security.biometric.BiometricAuthResult
import br.com.wgc.core.security.biometric.BiometricAuthStatus
import br.com.wgc.core.security.biometric.BiometricPromptConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class BiometricUiState(
    val title: String = "Módulo de Biometria, PIN e App Lock",
    val isBiometricReady: Boolean = false,
    val isAuthenticated: Boolean = false,
    val statusMessage: String = "Toque no botão para autenticar com biometria.",
    val errorMessage: String? = null
)

@HiltViewModel
class BiometricViewModel @Inject constructor(
    private val biometricAuthHelper: BiometricAuthHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(BiometricUiState())
    val uiState: StateFlow<BiometricUiState> = _uiState.asStateFlow()

    init {
        checkBiometricAvailability()
    }

    fun checkBiometricAvailability() {
        val status = biometricAuthHelper.canAuthenticate()
        val isReady = status == BiometricAuthStatus.Ready
        _uiState.update {
            it.copy(
                isBiometricReady = isReady,
                statusMessage = if (isReady) "Sensor biométrico pronto para uso." else "Biometria indisponível ou não configurada."
            )
        }
    }

    fun authenticate(activity: FragmentActivity, onResult: (Boolean) -> Unit = {}) {
        val status = biometricAuthHelper.canAuthenticate()
        if (status != BiometricAuthStatus.Ready) {
            _uiState.update {
                it.copy(errorMessage = "Biometria indisponível neste dispositivo.")
            }
            onResult(false)
            return
        }

        biometricAuthHelper.authenticate(
            activity = activity,
            config = BiometricPromptConfig(
                title = "Autenticação Biométrica WGC",
                subtitle = "Confirme sua identidade para prosseguir",
                negativeButtonText = "Cancelar"
            )
        ) { result ->
            when (result) {
                is BiometricAuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                            statusMessage = "Autenticado com sucesso via biometria!",
                            errorMessage = null
                        )
                    }
                    onResult(true)
                }
                is BiometricAuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAuthenticated = false,
                            errorMessage = result.errorMessage.toString()
                        )
                    }
                    onResult(false)
                }
                is BiometricAuthResult.Failed -> {
                    _uiState.update {
                        it.copy(
                            isAuthenticated = false,
                            errorMessage = "Biometria não reconhecida. Tente novamente."
                        )
                    }
                    onResult(false)
                }
                is BiometricAuthResult.Cancelled -> {
                    onResult(false)
                }
            }
        }
    }
}
