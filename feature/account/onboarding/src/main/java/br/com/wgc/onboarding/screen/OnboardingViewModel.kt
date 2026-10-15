package br.com.wgc.onboarding.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Representa um slide de conteúdo informativo do Onboarding.
 *
 * Encapsula o título de destaque, a descrição detalhada e uma tag de identificação
 * semântica utilizada pela interface gráfica para renderização de ícones temáticos.
 *
 * ### 📌 Quando Usar:
 * Utilize para criar e definir novos slides de boas-vindas na esteira de onboarding do app.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val page = OnboardingPage(
 *     title = "Entrega Ultrarrápida",
 *     description = "Receba seus pedidos em minutos com rastreamento GPS em tempo real.",
 *     tag = "TRACKING"
 * )
 * ```
 *
 * @property title Título do slide em destaque.
 * @property description Descrição detalhada do valor ou funcionalidade.
 * @property tag Identificador categórico para renderização do ícone.
 */
data class OnboardingPage(
    val title: String,
    val description: String,
    val tag: String
)

/**
 * Estado imutável de visualização da tela de Onboarding.
 *
 * Armazena a lista ordenada de páginas informativas, o índice do slide ativo
 * e o sinalizador de conclusão de apresentação.
 *
 * ### 📌 Quando Usar:
 * Consumido pela UI Compose para sincronizar os pontos indicadores (dots), botões de avanço
 * e transição automática para a tela de autenticação ou home.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val state = OnboardingUiState(
 *     currentPageIndex = 1,
 *     isCompleted = false
 * )
 * ```
 *
 * @property title Título global do Onboarding.
 * @property pages Lista de páginas que compõem a apresentação.
 * @property currentPageIndex Índice da página ativa no carrossel.
 * @property isCompleted Indica se o fluxo foi completado ou pulado.
 */
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

/**
 * ViewModel corporativo responsável pela navegação paginada e conclusão da experiência de Onboarding.
 *
 * Oferece métodos para avanço, retrocesso e encerramento antecipado do fluxo inicial de introdução.
 *
 * ### 📌 Quando Usar:
 * Instanciado pela tela [OnboardingScreen] no primeiro acesso do usuário para orquestrar
 * o carrossel de valor da marca.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDeApresentacao(
 *     viewModel: OnboardingViewModel = hiltViewModel()
 * ) {
 *     val state by viewModel.uiState.collectAsStateWithLifecycle()
 *     Button(onClick = viewModel::nextPage) {
 *         Text("Próximo")
 *     }
 * }
 * ```
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())

    /** Fluxo observável com o estado do carrossel de onboarding. */
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    /**
     * Avança para o próximo slide ou finaliza o onboarding se for o último slide da lista.
     *
     * ### 📌 Quando Usar:
     * Disparado quando o usuário toca no botão principal "Avançar" ou "Começar Agora".
     */
    fun nextPage() {
        _uiState.update { current ->
            if (current.currentPageIndex < current.pages.size - 1) {
                current.copy(currentPageIndex = current.currentPageIndex + 1)
            } else {
                current.copy(isCompleted = true)
            }
        }
    }

    /**
     * Retorna para o slide anterior do carrossel informativo.
     *
     * ### 📌 Quando Usar:
     * Disparado quando o usuário toca no botão secundário "Voltar".
     */
    fun previousPage() {
        _uiState.update { current ->
            if (current.currentPageIndex > 0) {
                current.copy(currentPageIndex = current.currentPageIndex - 1)
            } else {
                current
            }
        }
    }

    /**
     * Conclui imediatamente o onboarding ignorando os passos restantes.
     *
     * ### 📌 Quando Usar:
     * Acionado pelo botão "Pular" localizado no cabeçalho superior.
     */
    fun completeOnboarding() {
        _uiState.update { it.copy(isCompleted = true) }
    }

    /**
     * Reinicia o onboarding para o slide inicial e redefine o status de conclusão.
     *
     * ### 📌 Quando Usar:
     * Utilizado para testes manuais no sandbox de desenvolvedores ou quando o usuário
     * solicita a reexibição do tutorial no menu de configurações.
     */
    fun reset() {
        _uiState.update { it.copy(currentPageIndex = 0, isCompleted = false) }
    }
}