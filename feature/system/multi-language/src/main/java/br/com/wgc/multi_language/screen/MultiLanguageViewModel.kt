package br.com.wgc.multi_language.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.wgc.multi_language.locale.AppLanguage
import br.com.wgc.multi_language.locale.DynamicLocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de visualização da tela de seleção de idioma.
 *
 * @property title Título do módulo de internacionalização.
 * @property currentLanguage Idioma atualmente selecionado e ativo.
 * @property availableLanguages Lista de idiomas suportados pelo sistema.
 * @property isLoading Indica se a aplicação da localidade está em andamento.
 */
data class MultiLanguageUiState(
    val title: String = "Módulo de Internacionalização WGC",
    val currentLanguage: AppLanguage = AppLanguage.PORTUGUESE,
    val availableLanguages: List<AppLanguage> = AppLanguage.entries,
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pela gestão de localidades e troca de idioma em tempo de execução.
 *
 * ### 📌 Quando Usar:
 * - Na tela de configurações do aplicativo onde o usuário altera o idioma do sistema.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun MultiLanguageRoute(viewModel: MultiLanguageViewModel = hiltViewModel()) {
 *     val state by viewModel.uiState.collectAsState()
 *     // viewModel.selectLanguage(AppLanguage.ENGLISH)
 * }
 * ```
 *
 * @param localeManager Gerenciador dinâmico de idiomas e persistência de preferências de localidade.
 */
@HiltViewModel
class MultiLanguageViewModel @Inject constructor(
    private val localeManager: DynamicLocaleManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MultiLanguageUiState(currentLanguage = localeManager.currentLanguage.value)
    )

    /** Fluxo observável com as preferências de idioma ativas. */
    val uiState: StateFlow<MultiLanguageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            localeManager.currentLanguage.collect { lang ->
                _uiState.update { it.copy(currentLanguage = lang) }
            }
        }
    }

    /**
     * Altera o idioma ativo do aplicativo e atualiza as configurações em runtime.
     *
     * @param language [AppLanguage] selecionado pelo usuário.
     */
    fun selectLanguage(language: AppLanguage) {
        localeManager.setLanguage(language)
        _uiState.update { it.copy(currentLanguage = language, isLoading = false) }
    }
}