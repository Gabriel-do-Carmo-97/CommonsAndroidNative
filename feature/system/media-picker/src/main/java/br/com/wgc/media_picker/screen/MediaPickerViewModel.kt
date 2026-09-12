package br.com.wgc.media_picker.screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.wgc.omnibackend.core.repository.StorageRepository
import br.wgc.omnibackend.core.utils.DataResult
import br.wgc.omnibackend.core.utils.message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado da interface de seleção e transferência de arquivos multimídia.
 *
 * @property title Título identificador do componente de captura.
 * @property selectedMediaUri URI local do arquivo selecionado na galeria do dispositivo.
 * @property isUploading Indica se a transferência de dados para a nuvem está em execução.
 * @property uploadedUrl URL pública final gerada pelo serviço de armazenamento em nuvem.
 * @property errorMessage Mensagem de erro caso o upload falhe.
 */
data class MediaPickerUiState(
    val title: String = "Módulo de Captura de Mídia e QR Code",
    val selectedMediaUri: Uri? = null,
    val isUploading: Boolean = false,
    val uploadedUrl: String? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel que orquestra a seleção e upload de mídias através do [StorageRepository].
 *
 * @param storageRepository Repositório de arquivos do OmniBackend.
 */
@HiltViewModel
class MediaPickerViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaPickerUiState())

    /** Fluxo observável do estado do seletor de mídia. */
    val uiState: StateFlow<MediaPickerUiState> = _uiState.asStateFlow()

    /**
     * Registra a URI de mídia selecionada pelo usuário na galeria.
     *
     * @param uri URI local do arquivo escolhido.
     */
    fun onMediaSelected(uri: Uri) {
        _uiState.update { it.copy(selectedMediaUri = uri, errorMessage = null) }
    }

    /**
     * Envia o arquivo selecionado para o destino remoto especificado.
     *
     * @param targetPath Subdiretório no bucket de armazenamento remoto.
     */
    fun uploadMedia(targetPath: String = "commons_uploads") {
        val uri = _uiState.value.selectedMediaUri ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, errorMessage = null) }
            storageRepository.uploadFile(targetPath, uri).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                uploadedUrl = result.data.toString(),
                                errorMessage = null
                            )
                        }
                    }
                    is DataResult.Failure -> {
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                errorMessage = result.error.message ?: "Falha ao enviar arquivo"
                            )
                        }
                    }
                }
            }
        }
    }
}