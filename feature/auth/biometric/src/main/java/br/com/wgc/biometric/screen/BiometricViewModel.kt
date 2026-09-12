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

/**
 * Estado de interface da tela de autenticação biométrica e segurança.
 *
 * @property title Título informativo do cabeçalho da tela.
 * @property isBiometricReady Indica se os sensores biométricos do aparelho estão operacionais.
 * @property isAuthenticated Indica se o usuário concluiu a verificação biométrica com sucesso.
 * @property statusMessage Mensagem contextual sobre o status atual dos sensores.
 * @property errorMessage Mensagem de erro caso haja falha ou cancelamento da operação.
 */
data class BiometricUiState(
    val title: String = "Módulo de Biometria, PIN e App Lock",
    val isBiometricReady: Boolean = false,
    val isAuthenticated: Boolean = false,
    val statusMessage: String = "Toque no botão para autenticar com biometria.",
    val errorMessage: String? = null
)

/**
 * ViewModel responsável pela integração com o sensor biométrico (BiometricPrompt) do dispositivo.
 *
 * @param biometricAuthHelper Helper de segurança provido pelo CoreAndroidNative.
 */
@HiltViewModel
class BiometricViewModel @Inject constructor(
    private val biometricAuthHelper: BiometricAuthHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(BiometricUiState())

    /** Fluxo de estado da UI com as informações de segurança biométrica. */
    val uiState: StateFlow<BiometricUiState> = _uiState.asStateFlow()

    init {
        checkBiometricAvailability()
    }

    /**
     * Verifica a disponibilidade de hardware e credenciais biométricas registradas no aparelho.
     */
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

    /**
     * Executa a chamada do BiometricPrompt nativo para autenticar o usuário.
     *
     * @param activity Contexto de FragmentActivity onde o diálogo biométrico é renderizado.
     * @param onResult Callback com o resultado booleano da autenticação.
     */
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