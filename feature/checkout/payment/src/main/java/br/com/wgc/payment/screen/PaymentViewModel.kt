package br.com.wgc.payment.screen

import br.com.wgc.ds_templates.screens.home.fintech.BaseFintechHomeViewModel
import br.com.wgc.ds_templates.screens.home.fintech.FintechHomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel responsável pelo estado da carteira e pagamentos da aplicação.
 *
 * Fornece ações para alternar privacidade do saldo, inicializar transações Pix
 * e transferências bancárias.
 */
@HiltViewModel
class PaymentViewModel @Inject constructor() : BaseFintechHomeViewModel() {

    private val sampleTransactions = listOf(
        "Pagamento via Pix - WGC Store (-R$ 54,90)",
        "Transferência Recebida (+R$ 150,00)",
        "Compra Cartão de Crédito - Padaria (-R$ 22,50)",
        "Cashback Programa Fidelidade (+R$ 10,00)"
    )

    private val _uiState = MutableStateFlow(
        FintechHomeUiState(
            userName = "Gabriel do Carmo",
            balance = "R$ 1.842,50",
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
     */
    override fun onToggleBalanceVisibility() {
        _uiState.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
    }

    /**
     * Dispara o fluxo de pagamento instantâneo via Pix (Copia e Cola / QR Code).
     */
    override fun onPixClick() {
        // Disparar fluxo de Pix (Copia e Cola / QR Code)
    }

    /**
     * Dispara o fluxo de transferência bancária entre contas.
     */
    override fun onTransferClick() {
        // Disparar fluxo de transferência bancária
    }
}