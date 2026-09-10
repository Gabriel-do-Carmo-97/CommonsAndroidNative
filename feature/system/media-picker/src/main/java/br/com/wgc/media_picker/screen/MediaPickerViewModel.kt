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
 * Estado da interface de seleÃ§Ã£o e transferÃªncia de arquivos multimÃ­dia.
 *
 * @property title TÃ­tulo identificador do componente de captura.
 * @property selectedMediaUri URI local do arquivo selecionado na galeria do dispositivo.
 * @property isUploading Indica se a transferÃªncia de dados para a nuvem estÃ¡ em execuÃ§Ã£o.
 * @property uploadedUrl URL pÃºblica final gerada pelo serviÃ§o de armazenamento em nuvem.
 * @property errorMessage Mensagem de erro caso o upload falhe.
 */
data class MediaPickerUiState(
    val title: String = "MÃ³dulo de Captura de MÃ­dia e QR Code",
    val selectedMediaUri: Uri? = null,
    val isUploading: Boolean = false,
    val uploadedUrl: String? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel que orquestra a seleÃ§Ã£o e upload de mÃ­dias atravÃ©s do [StorageRepository].
 *
 * @param storageRepository RepositÃ³rio de arquivos do OmniBackend.
 */
@HiltViewModel
class MediaPickerViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaPickerUiState())

    /** Fluxo observÃ¡vel do estado do seletor de mÃ­dia. */
    val uiState: StateFlow<MediaPickerUiState> = _uiState.asStateFlow()

    /**
     * Registra a URI de mÃ­dia selecionada pelo usuÃ¡rio na galeria.
     *
     * @param uri URI local do arquivo escolhido.
     */
    fun onMediaSelected(uri: Uri) {
        _uiState.update { it.copy(selectedMediaUri = uri, errorMessage = null) }
    }

    /**
     * Envia o arquivo selecionado para o destino remoto especificado.
     *
     * @param targetPath SubdiretÃ³rio no bucket de armazenamento remoto.
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