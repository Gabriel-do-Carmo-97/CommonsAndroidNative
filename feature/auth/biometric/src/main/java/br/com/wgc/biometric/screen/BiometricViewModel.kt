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
 * Estado de interface da tela de autenticaÃ§Ã£o biomÃ©trica e seguranÃ§a.
 *
 * @property title TÃ­tulo informativo do cabeÃ§alho da tela.
 * @property isBiometricReady Indica se os sensores biomÃ©tricos do aparelho estÃ£o operacionais.
 * @property isAuthenticated Indica se o usuÃ¡rio concluiu a verificaÃ§Ã£o biomÃ©trica com sucesso.
 * @property statusMessage Mensagem contextual sobre o status atual dos sensores.
 * @property errorMessage Mensagem de erro caso haja falha ou cancelamento da operaÃ§Ã£o.
 */
data class BiometricUiState(
    val title: String = "MÃ³dulo de Biometria, PIN e App Lock",
    val isBiometricReady: Boolean = false,
    val isAuthenticated: Boolean = false,
    val statusMessage: String = "Toque no botÃ£o para autenticar com biometria.",
    val errorMessage: String? = null
)

/**
 * ViewModel responsÃ¡vel pela integraÃ§Ã£o com o sensor biomÃ©trico (BiometricPrompt) do dispositivo.
 *
 * @param biometricAuthHelper Helper de seguranÃ§a provido pelo CoreAndroidNative.
 */
@HiltViewModel
class BiometricViewModel @Inject constructor(
    private val biometricAuthHelper: BiometricAuthHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(BiometricUiState())

    /** Fluxo de estado da UI com as informaÃ§Ãµes de seguranÃ§a biomÃ©trica. */
    val uiState: StateFlow<BiometricUiState> = _uiState.asStateFlow()

    init {
        checkBiometricAvailability()
    }

    /**
     * Verifica a disponibilidade de hardware e credenciais biomÃ©tricas registradas no aparelho.
     */
    fun checkBiometricAvailability() {
        val status = biometricAuthHelper.canAuthenticate()
        val isReady = status == BiometricAuthStatus.Ready
        _uiState.update {
            it.copy(
                isBiometricReady = isReady,
                statusMessage = if (isReady) "Sensor biomÃ©trico pronto para uso." else "Biometria indisponÃ­vel ou nÃ£o configurada."
            )
        }
    }

    /**
     * Executa a chamada do BiometricPrompt nativo para autenticar o usuÃ¡rio.
     *
     * @param activity Contexto de FragmentActivity onde o diÃ¡logo biomÃ©trico Ã© renderizado.
     * @param onResult Callback com o resultado booleano da autenticaÃ§Ã£o.
     */
    fun authenticate(activity: FragmentActivity, onResult: (Boolean) -> Unit = {}) {
        val status = biometricAuthHelper.canAuthenticate()
        if (status != BiometricAuthStatus.Ready) {
            _uiState.update {
                it.copy(errorMessage = "Biometria indisponÃ­vel neste dispositivo.")
            }
            onResult(false)
            return
        }

        biometricAuthHelper.authenticate(
            activity = activity,
            config = BiometricPromptConfig(
                title = "AutenticaÃ§Ã£o BiomÃ©trica WGC",
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
                            errorMessage = "Biometria nÃ£o reconhecida. Tente novamente."
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