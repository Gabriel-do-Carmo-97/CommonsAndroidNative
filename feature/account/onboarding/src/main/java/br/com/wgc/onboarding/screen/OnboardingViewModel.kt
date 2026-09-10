package br.com.wgc.onboarding.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class OnboardingPage(
    val title: String,
    val description: String,
    val tag: String
)

data class OnboardingUiState(
    val title: String = "Bem-vindo ao Ecossistema WGC",
    val pages: List<OnboardingPage> = listOf(
        OnboardingPage(
            title = "Catálogo & Compras Inteligentes",
            description = "Explore centenas de produtos, utilize filtros reativos com debounce e monte seu carrinho com facilidade.",
            tag = "E-COMMERCE"
        ),
        OnboardingPage(
            title = "Rastreamento em Tempo Real",
            description = "Acompanhe seus pedidos e serviços com geolocalização ao vivo e previsão precisa de chegada.",
            tag = "TRACKING"
        ),
        OnboardingPage(
            title = "Pagamentos Rápidos & Seguros",
            description = "Pague via Pix instantâneo, cartão de crédito ou utilize seu saldo de cashback e pontos de fidelidade.",
            tag = "FINTECH"
        )
    ),
    val currentPageIndex: Int = 0,
    val isCompleted: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextPage() {
        _uiState.update { current ->
            if (current.currentPageIndex < current.pages.size - 1) {
                current.copy(currentPageIndex = current.currentPageIndex + 1)
            } else {
                current.copy(isCompleted = true)
            }
        }
    }

    fun previousPage() {
        _uiState.update { current ->
            if (current.currentPageIndex > 0) {
                current.copy(currentPageIndex = current.currentPageIndex - 1)
            } else {
                current
            }
        }
    }

    fun completeOnboarding() {
        _uiState.update { it.copy(isCompleted = true) }
    }

    fun reset() {
        _uiState.update { it.copy(currentPageIndex = 0, isCompleted = false) }
    }
}
