package br.com.wgc.cart.screen

import androidx.lifecycle.viewModelScope
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.formatters.toCurrencyFormatted
import br.com.wgc.design_system.factories.cart.templates.BaseStandardCartViewModel
import br.com.wgc.design_system.factories.cart.templates.CartItem
import br.com.wgc.design_system.factories.cart.templates.StandardCartUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel corporativo responsável pelo ciclo de vida, estado reativo e regras de negócio do Carrinho de Compras.
 *
 * Provê métodos para adição e remoção de mercadorias, recálculo dinâmico de taxa de entrega baseado em CEP,
 * formatação monetária corporativa através de [toCurrencyFormatted] e disparo da intenção de finalização da compra.
 *
 * ### 📌 Quando Usar:
 * Utilize este ViewModel ao montar ou exibir qualquer variante de carrinho (`CartScreen`),
 * gerenciando o estado local dos itens selecionados antes de avançar para a etapa de pagamento.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDoCarrinho(
 *     viewModel: CartViewModel = hiltViewModel()
 * ) {
 *     val state by viewModel.uiState.collectAsState()
 *
 *     // Adicionar item dinamicamente
 *     Button(onClick = {
 *         viewModel.addItem(CartItem(id = "10", title = "Hambúrguer Artesanal", price = 38.00.toCurrencyFormatted()))
 *     }) {
 *         Text("Adicionar Item")
 *     }
 *
 *     StandardCartScreenTemplate(viewModel = viewModel)
 * }
 * ```
 *
 * @param dispatchers Provedor unificado de CoroutineDispatchers do CoreAndroidNative.
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val dispatchers: CoroutineDispatchers
) : BaseStandardCartViewModel() {

    private val defaultItems = listOf(
        CartItem(id = "1", title = "Pizza Calabresa Especial (Grande)", price = 54.90.toCurrencyFormatted()),
        CartItem(id = "2", title = "Refrigerante Guaraná Antarctica 2L", price = 12.00.toCurrencyFormatted()),
        CartItem(id = "3", title = "Borda Recheada Catupiry Original", price = 8.50.toCurrencyFormatted())
    )

    private val _uiState = MutableStateFlow(
        StandardCartUiState(
            items = defaultItems,
            total = calculateTotal(defaultItems, 0.0),
            isLoading = false
        )
    )

    /**
     * Fluxo observável com o estado atualizado do carrinho de compras.
     */
    override val uiState: StateFlow<StandardCartUiState> = _uiState.asStateFlow()

    private var deliveryFee: Double = 0.0

    /**
     * Dispara a intenção de checkout/fechamento do pedido simulando processamento assíncrono.
     *
     * ### 📌 Quando Usar:
     * Acionado pelo usuário ao clicar no botão de finalizar pedido do carrinho.
     */
    override fun onCheckoutClick() {
        viewModelScope.launch(dispatchers.main) {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Adiciona um novo item ao carrinho recalculando o valor total.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário adiciona produtos da vitrine ou de sugestões ao carrinho.
     *
     * @param item Objeto [CartItem] representando a mercadoria ou serviço a ser adicionado.
     */
    fun addItem(item: CartItem) {
        val updatedList = _uiState.value.items + item
        _uiState.update {
            it.copy(
                items = updatedList,
                total = calculateTotal(updatedList, deliveryFee)
            )
        }
    }

    /**
     * Remove um item específico do carrinho através do seu identificador.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário clica no ícone de lixeira ou reduz a quantidade de um item para zero.
     *
     * @param itemId Identificador único do item a ser removido.
     */
    fun removeItem(itemId: String) {
        val updatedList = _uiState.value.items.filterNot { it.id == itemId }
        _uiState.update {
            it.copy(
                items = updatedList,
                total = calculateTotal(updatedList, deliveryFee)
            )
        }
    }

    /**
     * Aplica uma taxa de entrega calculada dinamicamente conforme a faixa de CEP fornecida.
     *
     * ### 📌 Quando Usar:
     * Acionado após a consulta de frete pelo CEP digitado pelo usuário.
     *
     * @param zipCode Código postal do endereço de entrega.
     */
    fun applyDeliveryFee(zipCode: String) {
        deliveryFee = if (zipCode.startsWith("01") || zipCode.startsWith("04")) 5.0 else 10.0
        _uiState.update {
            it.copy(total = calculateTotal(it.items, deliveryFee))
        }
    }

    /**
     * Realiza a soma dos valores dos itens somando a taxa de entrega e formatando em moeda corrente (BRL).
     *
     * @param items Lista de itens presentes no carrinho.
     * @param fee Valor da taxa de entrega a ser somado.
     * @return String formatada com o valor monetário total.
     */
    private fun calculateTotal(items: List<CartItem>, fee: Double): String {
        var sum = fee
        for (item in items) {
            val numericPrice = item.price
                .replace("R$", "")
                .replace(" ", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
                .toDoubleOrNull() ?: 0.0
            sum += numericPrice
        }
        return sum.toCurrencyFormatted()
    }
}