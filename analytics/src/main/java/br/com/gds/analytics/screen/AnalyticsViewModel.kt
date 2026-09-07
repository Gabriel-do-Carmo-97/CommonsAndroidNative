package br.com.gds.analytics.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class AnalyticsUiState(val title: String = "Módulo de Analytics e Dashboard WGC", val isLoading: Boolean = false)

@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState
}
