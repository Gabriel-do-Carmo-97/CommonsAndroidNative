package br.com.wgc.onboarding.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Representa um slide de conteÃºdo informativo do Onboarding.
 *
 * @property title TÃ­tulo do slide em destaque.
 * @property description DescriÃ§Ã£o detalhada do valor ou funcionalidade.
 * @property tag Identificador categÃ³rico para renderizaÃ§Ã£o do Ã­cone.
 */
data class OnboardingPage(
    val title: String,
    val description: String,
    val tag: String
)

/**
 * Estado de visualizaÃ§Ã£o da tela de Onboarding.
 *
 * @property title TÃ­tulo global do Onboarding.
 * @property pages Lista de pÃ¡ginas que compÃµem a apresentaÃ§Ã£o.
 * @property currentPageIndex Ãndice da pÃ¡gina ativa no carrossel.
 * @property isCompleted Indica se o fluxo foi completado ou pulado.
 */
data class OnboardingUiState(
    val title: String = "Bem-vindo ao Ecossistema WGC",
    val pages: List<OnboardingPage> = listOf(
        OnboardingPage(
            title = "CatÃ¡logo & Compras Inteligentes",
            description = "Explore centenas de produtos, utilize filtros reativos com debounce e monte seu carrinho com facilidade.",
            tag = "E-COMMERCE"
        ),
        OnboardingPage(
            title = "Rastreamento em Tempo Real",
            description = "Acompanhe seus pedidos e serviÃ§os com geolocalizaÃ§Ã£o ao vivo e previsÃ£o precisa de chegada.",
            tag = "TRACKING"
        ),
        OnboardingPage(
            title = "Pagamentos RÃ¡pidos & Seguros",
            description = "Pague via Pix instantÃ¢neo, cartÃ£o de crÃ©dito ou utilize seu saldo de cashback e pontos de fidelidade.",
            tag = "FINTECH"
        )
    ),
    val currentPageIndex: Int = 0,
    val isCompleted: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pela navegaÃ§Ã£o paginada e conclusÃ£o da experiÃªncia de Onboarding.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())

    /** Fluxo observÃ¡vel com o estado do carrossel de onboarding. */
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    /** AvanÃ§a para o prÃ³ximo slide ou finaliza o onboarding se for o Ãºltimo. */
    fun nextPage() {
        _uiState.update { current ->
            if (current.currentPageIndex < current.pages.size - 1) {
                current.copy(currentPageIndex = current.currentPageIndex + 1)
            } else {
                current.copy(isCompleted = true)
            }
        }
    }

    /** Retorna para o slide anterior do carrossel. */
    fun previousPage() {
        _uiState.update { current ->
            if (current.currentPageIndex > 0) {
                current.copy(currentPageIndex = current.currentPageIndex - 1)
            } else {
                current
            }
        }
    }

    /** Conclui imediatamente o onboarding ignorando os passos restantes. */
    fun completeOnboarding() {
        _uiState.update { it.copy(isCompleted = true) }
    }

    /** Reinicia o onboarding para o slide inicial. */
    fun reset() {
        _uiState.update { it.copy(currentPageIndex = 0, isCompleted = false) }
    }
}