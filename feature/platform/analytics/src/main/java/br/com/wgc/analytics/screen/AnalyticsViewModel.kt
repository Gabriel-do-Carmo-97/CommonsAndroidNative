package br.com.wgc.analytics.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do dashboard analÃ­tico e mÃ©tricas corporativas.
 *
 * @property title TÃ­tulo do painel analÃ­tico.
 * @property isLoading Indica se a agregaÃ§Ã£o de telemetria e mÃ©tricas estÃ¡ carregando.
 */
data class AnalyticsUiState(
    val title: String = "MÃ³dulo de Analytics e Dashboard WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel que consolida eventos comportamentais e indicadores operacionais de negÃ³cio.
 */
@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())

    /** Fluxo de estado contendo mÃ©tricas agregadas e dados de BI. */
    val uiState: StateFlow<AnalyticsUiState> = _uiState
}