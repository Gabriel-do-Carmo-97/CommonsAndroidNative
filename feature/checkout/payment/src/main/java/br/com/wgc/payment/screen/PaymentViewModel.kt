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
 * ViewModel responsÃ¡vel pelo estado da carteira e pagamentos da aplicaÃ§Ã£o.
 *
 * Fornece aÃ§Ãµes para alternar privacidade do saldo, inicializar transaÃ§Ãµes Pix
 * e transferÃªncias bancÃ¡rias.
 */
@HiltViewModel
class PaymentViewModel @Inject constructor() : BaseFintechHomeViewModel() {

    private val sampleTransactions = listOf(
        "Pagamento via Pix - WGC Store (-R$ 54,90)",
        "TransferÃªncia Recebida (+R$ 150,00)",
        "Compra CartÃ£o de CrÃ©dito - Padaria (-R$ 22,50)",
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
     * Fluxo observÃ¡vel com o estado do painel financeiro e saldo da conta.
     */
    override val uiState: StateFlow<FintechHomeUiState> = _uiState.asStateFlow()

    /**
     * Alterna a visibilidade pÃºblica do saldo na interface.
     */
    override fun onToggleBalanceVisibility() {
        _uiState.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
    }

    /**
     * Dispara o fluxo de pagamento instantÃ¢neo via Pix (Copia e Cola / QR Code).
     */
    override fun onPixClick() {
        // Disparar fluxo de Pix (Copia e Cola / QR Code)
    }

    /**
     * Dispara o fluxo de transferÃªncia bancÃ¡ria entre contas.
     */
    override fun onTransferClick() {
        // Disparar fluxo de transferÃªncia bancÃ¡ria
    }
}