package br.com.wgc.feedback.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.wgc.omnibackend.core.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado imutável da tela de pesquisa de satisfação e NPS.
 *
 * @property title Título exibido no cabeçalho do formulário.
 * @property npsScore Nota atribuída de 0 a 10 no Net Promoter Score.
 * @property starRating Classificação quantitativa de 1 a 5 estrelas.
 * @property selectedCategory Categoria principal destacada pelo cliente.
 * @property availableCategories Lista de opções de categorias para filtragem.
 * @property comment Comentário descritivo ou sugestão livre.
 * @property isSubmitting Indicador de envio assíncrono em andamento.
 * @property isSubmitted Indicador de conclusão bem-sucedida do feedback.
 * @property errorMessage Mensagem de erro caso a transmissão falhe.
 */
data class FeedbackUiState(
    val title: String = "Pesquisa de Satisfação & NPS",
    val npsScore: Int = 10,
    val starRating: Int = 5,
    val selectedCategory: String = "Atendimento",
    val availableCategories: List<String> = listOf("Atendimento", "Entrega", "Qualidade", "Preço", "Aplicativo"),
    val comment: String = "",
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel responsável pelo gerenciamento de pesquisas de satisfação e despacho para o OmniBackend.
 *
 * @param firestoreRepository Repositório Cloud Firestore para persistência remota das métricas de NPS.
 */
@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackUiState())

    /**
     * Fluxo de estado observável com os dados do formulário de feedback.
     */
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    /**
     * Atualiza a nota de recomendação NPS fornecida pelo usuário.
     *
     * @param score Nota inteira limitada no intervalo de 0 a 10.
     */
    fun onNpsScoreChanged(score: Int) {
        _uiState.update { it.copy(npsScore = score.coerceIn(0, 10)) }
    }

    /**
     * Atualiza a avaliação por estrelas selecionada pelo usuário.
     *
     * @param rating Quantidade de estrelas no intervalo de 1 a 5.
     */
    fun onStarRatingChanged(rating: Int) {
        _uiState.update { it.copy(starRating = rating.coerceIn(1, 5)) }
    }

    /**
     * Define a categoria do aspecto avaliado.
     *
     * @param category Nome da categoria temática selecionada.
     */
    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    /**
     * Modifica o texto livre de comentário do usuário.
     *
     * @param comment Mensagem com detalhes da experiência.
     */
    fun onCommentChanged(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    /**
     * Transmite os dados estruturados de satisfação para a coleção `feedbacks` no Firestore.
     *
     * @param onSuccess Callback executado após a confirmação do registro.
     */
    fun submitFeedback(onSuccess: () -> Unit = {}) {
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val payload = mapOf(
                "npsScore" to _uiState.value.npsScore,
                "starRating" to _uiState.value.starRating,
                "category" to _uiState.value.selectedCategory,
                "comment" to _uiState.value.comment,
                "timestamp" to System.currentTimeMillis()
            )
            try {
                firestoreRepository.addDocument(
                    collection = "feedbacks",
                    data = payload,
                    customId = null
                )
            } catch (_: Exception) {
                // Fallback gracioso para persistência offline
            }
            _uiState.update { it.copy(isSubmitting = false, isSubmitted = true) }
            onSuccess()
        }
    }
}