package br.com.gds.scheduling.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SchedulingUiState(val title: String = "Módulo de Agendamento de Serviços", val isLoading: Boolean = false)

@HiltViewModel
class SchedulingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SchedulingUiState())
    val uiState: StateFlow<SchedulingUiState> = _uiState
}
