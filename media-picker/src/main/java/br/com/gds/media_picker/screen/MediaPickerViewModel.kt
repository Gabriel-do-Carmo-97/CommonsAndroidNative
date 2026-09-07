package br.com.gds.media_picker.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class MediaPickerUiState(val title: String = "Módulo de Captura de Mídia e QR Code", val isLoading: Boolean = false)

@HiltViewModel
class MediaPickerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MediaPickerUiState())
    val uiState: StateFlow<MediaPickerUiState> = _uiState
}
