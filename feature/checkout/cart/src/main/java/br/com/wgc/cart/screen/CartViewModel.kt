package br.com.wgc.cart.screen

import androidx.lifecycle.viewModelScope
import br.com.wgc.ds_templates.screens.cart.BaseStandardCartViewModel
import br.com.wgc.ds_templates.screens.cart.CartItem
import br.com.wgc.ds_templates.screens.cart.StandardCartUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel responsÃ¡vel pela lÃ³gica de negÃ³cios e estado reativo do Carrinho de Compras.
 *
 * Fornece mÃ©todos para adiÃ§Ã£o e remoÃ§Ã£o de itens, cÃ¡lculo de taxa de entrega baseado em CEP
 * e disparo da intenÃ§Ã£o de finalizaÃ§Ã£o da compra.
 */
@HiltViewModel
class CartViewModel @Inject constructor() : BaseStandardCartViewModel() {

    private val defaultItems = listOf(
        CartItem(id = "1", title = "Pizza Calabresa Especial (Grande)", price = "R$ 54,90"),
        CartItem(id = "2", title = "Refrigerante GuaranÃ¡ Antarctica 2L", price = "R$ 12,00"),
        CartItem(id = "3", title = "Borda Recheada Catupiry Original", price = "R$ 8,50")
    )

    private val _uiState = MutableStateFlow(
        StandardCartUiState(
            items = defaultItems,
            total = calculateTotal(defaultItems, 0.0),
            isLoading = false
        )
    )

    /**
     * Fluxo observÃ¡vel com o estado atualizado do carrinho de compras.
     */
    override val uiState: StateFlow<StandardCartUiState> = _uiState.asStateFlow()

    private var deliveryFee: Double = 0.0

    /**
     * Dispara a intenÃ§Ã£o de checkout/fechamento do pedido simulando processamento assÃ­ncrono.
     */
    override fun onCheckoutClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Adiciona um novo item ao carrinho recalculando o valor total.
     *
     * @param item Objeto [CartItem] representando a mercadoria ou serviÃ§o a ser adicionado.
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
     * Remove um item especÃ­fico do carrinho atravÃ©s do seu identificador.
     *
     * @param itemId Identificador Ãºnico do item a ser removido.
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
     * @param zipCode CÃ³digo postal do endereÃ§o de entrega.
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
     * @return String formatada com o valor monetÃ¡rio total.
     */
    private fun calculateTotal(items: List<CartItem>, fee: Double): String {
        var sum = fee
        for (item in items) {
            val numericPrice = item.price
                .replace("R$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
                .toDoubleOrNull() ?: 0.0
            sum += numericPrice
        }
        return String.format(Locale.GERMANY, "R$ %.2f", sum)
    }
}