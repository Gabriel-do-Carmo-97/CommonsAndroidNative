package br.com.wgc.multi_language.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado de visualizaÃ§Ã£o da tela de seleÃ§Ã£o de idioma.
 *
 * @property title TÃ­tulo do mÃ³dulo de internacionalizaÃ§Ã£o.
 * @property isLoading Indica se a aplicaÃ§Ã£o da localidade estÃ¡ em andamento.
 */
data class MultiLanguageUiState(
    val title: String = "MÃ³dulo de InternacionalizaÃ§Ã£o WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pela gestÃ£o de localidades e troca de idioma em tempo de execuÃ§Ã£o.
 */
@HiltViewModel
class MultiLanguageViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MultiLanguageUiState())

    /** Fluxo observÃ¡vel com as preferÃªncias de idioma ativas. */
    val uiState: StateFlow<MultiLanguageUiState> = _uiState
}