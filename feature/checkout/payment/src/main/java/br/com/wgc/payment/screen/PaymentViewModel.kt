package br.com.wgc.payment.screen

import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.formatters.toCurrencyFormatted
import br.com.wgc.design_system.templates.screens.home.fintech.BaseFintechHomeViewModel
import br.com.wgc.design_system.templates.screens.home.fintech.FintechHomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import br.com.wgc.payment.navigation.PaymentNavDestinations
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel corporativo responsável pelo painel de pagamentos, carteira digital e checkout financeiro.
 *
 * Gerencia a visibilidade do saldo disponível formatado via utilitários corporativos [toCurrencyFormatted],
 * extrato de transações recentes, e despacha fluxos transacionais como transferências, Pix e checkout.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela financeira principal (`PaymentScreen`) ou no fechamento de checkout
 * para controlar métodos de pagamento e saldo do cliente com formatação monetária padrão BRL.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun CarteiraDigital(
 *     viewModel: PaymentViewModel = hiltViewModel()
 * ) {
 *     val uiState by viewModel.uiState.collectAsState()
 *
 *     // Alternar exibição do saldo
 *     IconButton(onClick = { viewModel.onToggleBalanceVisibility() }) {
 *         Icon(...)
 *     }
 * }
 * ```
 *
 * @param dispatchers Provedor centralizado de CoroutineDispatchers do CoreAndroidNative.
 */
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val dispatchers: CoroutineDispatchers
) : BaseFintechHomeViewModel() {

    private val _navigationEvent = Channel<PaymentNavDestinations>(Channel.BUFFERED)

    /**
     * Fluxo de navegação para roteamento seguro de operações financeiras.
     */
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val sampleTransactions = listOf(
        "Pagamento via Pix - WGC Store (-${54.90.toCurrencyFormatted()})",
        "Transferência Recebida (+${150.00.toCurrencyFormatted()})",
        "Compra Cartão de Crédito - Padaria (-${22.50.toCurrencyFormatted()})",
        "Cashback Programa Fidelidade (+${10.00.toCurrencyFormatted()})"
    )

    private val _uiState = MutableStateFlow(
        FintechHomeUiState(
            userName = "Gabriel do Carmo",
            balance = 1842.50.toCurrencyFormatted(),
            isBalanceVisible = true,
            transactions = sampleTransactions
        )
    )

    /**
     * Fluxo observável com o estado do painel financeiro e saldo da conta.
     */
    override val uiState: StateFlow<FintechHomeUiState> = _uiState.asStateFlow()

    /**
     * Alterna a visibilidade pública do saldo na interface.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário toca no ícone de "olho" para ocultar ou revelar seu saldo monetário.
     */
    override fun onToggleBalanceVisibility() {
        _uiState.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
    }

    /**
     * Dispara o fluxo de pagamento instantâneo via Pix (Copia e Cola / QR Code).
     *
     * ### 📌 Quando Usar:
     * Acionado ao clicar no atalho de pagamento Pix na interface da carteira.
     */
    override fun onPixClick() {
        viewModelScope.launch(dispatchers.main) {
            _navigationEvent.send(PaymentNavDestinations.Checkout)
        }
    }

    /**
     * Dispara o fluxo de transferência bancária entre contas.
     *
     * ### 📌 Quando Usar:
     * Acionado ao selecionar transferência entre usuários ou contas bancárias.
     */
    override fun onTransferClick() {
        viewModelScope.launch(dispatchers.main) {
            _navigationEvent.send(PaymentNavDestinations.Checkout)
        }
    }

    /**
     * Redireciona o usuário para o histórico completo de transações e extrato financeiro.
     *
     * ### 📌 Quando Usar:
     * Disparado quando o usuário toca na opção "Ver todas as transações".
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * viewModel.onViewHistoryClick("usr_12345")
     * ```
     *
     * @param userId Identificador único do usuário associado à conta.
     */
    fun onViewHistoryClick(userId: String) {
        viewModelScope.launch(dispatchers.main) {
            _navigationEvent.send(PaymentNavDestinations.PaymentHistory(userId))
        }
    }
}