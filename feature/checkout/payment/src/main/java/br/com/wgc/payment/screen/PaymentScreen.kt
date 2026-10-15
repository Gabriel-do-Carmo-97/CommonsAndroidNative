package br.com.wgc.payment.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.design_system.factories.home.templates.fintech.FintechHomeScreenTemplate

/**
 * Tela principal de Pagamento e Carteira Digital (Fintech).
 *
 * Exibe saldo disponível, ações rápidas (Pix, Transferência) e extrato recente de transações,
 * utilizando o template estruturado [FintechHomeScreenTemplate] do Design System.
 *
 * ### 📌 Quando Usar:
 * Renderize esta tela dentro de abas financeiras, carteiras in-app ou no fluxo de checkout
 * onde o usuário precise visualizar seu saldo e movimentações monetárias.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun AbaFinanceira() {
 *     PaymentScreen(
 *         modifier = Modifier.fillMaxSize()
 *     )
 * }
 * ```
 *
 * @param modifier Modificador de layout Compose a ser aplicado na raiz da tela.
 * @param viewModel ViewModel injetado com gerenciamento do saldo e extrato financeiro.
 */
@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    FintechHomeScreenTemplate(
        viewModel = viewModel
    )
}