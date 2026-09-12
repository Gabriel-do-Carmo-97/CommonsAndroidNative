package br.com.wgc.analytics.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do dashboard analítico e métricas corporativas.
 *
 * @property title Título do painel analítico.
 * @property isLoading Indica se a agregação de telemetria e métricas está carregando.
 */
data class AnalyticsUiState(
    val title: String = "Módulo de Analytics e Dashboard WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel que consolida eventos comportamentais e indicadores operacionais de negócio.
 */
@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())

    /** Fluxo de estado contendo métricas agregadas e dados de BI. */
    val uiState: StateFlow<AnalyticsUiState> = _uiState
}