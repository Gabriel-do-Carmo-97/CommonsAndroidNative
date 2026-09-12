package br.com.wgc.multi_language.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado de visualização da tela de seleção de idioma.
 *
 * @property title Título do módulo de internacionalização.
 * @property isLoading Indica se a aplicação da localidade está em andamento.
 */
data class MultiLanguageUiState(
    val title: String = "Módulo de Internacionalização WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pela gestão de localidades e troca de idioma em tempo de execução.
 */
@HiltViewModel
class MultiLanguageViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MultiLanguageUiState())

    /** Fluxo observável com as preferências de idioma ativas. */
    val uiState: StateFlow<MultiLanguageUiState> = _uiState
}