package br.com.wgc.payment.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.home.fintech.FintechHomeScreenTemplate

/**
 * Tela principal de Pagamento e Carteira Digital (Fintech).
 *
 * Exibe saldo disponÃ­vel, aÃ§Ãµes rÃ¡pidas (Pix, TransferÃªncia) e extrato recente de transaÃ§Ãµes,
 * utilizando o template estruturado [FintechHomeScreenTemplate].
 *
 * @param modifier Modificador de layout Compose a ser aplicado na raiz.
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