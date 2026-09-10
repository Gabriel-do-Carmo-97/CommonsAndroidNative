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

@HiltViewModel
class CartViewModel @Inject constructor() : BaseStandardCartViewModel() {

    private val defaultItems = listOf(
        CartItem(id = "1", title = "Pizza Calabresa Especial (Grande)", price = "R$ 54,90"),
        CartItem(id = "2", title = "Refrigerante Guaraná Antarctica 2L", price = "R$ 12,00"),
        CartItem(id = "3", title = "Borda Recheada Catupiry Original", price = "R$ 8,50")
    )

    private val _uiState = MutableStateFlow(
        StandardCartUiState(
            items = defaultItems,
            total = calculateTotal(defaultItems, 0.0),
            isLoading = false
        )
    )
    override val uiState: StateFlow<StandardCartUiState> = _uiState.asStateFlow()

    private var deliveryFee: Double = 0.0

    override fun onCheckoutClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun addItem(item: CartItem) {
        val updatedList = _uiState.value.items + item
        _uiState.update {
            it.copy(
                items = updatedList,
                total = calculateTotal(updatedList, deliveryFee)
            )
        }
    }

    fun removeItem(itemId: String) {
        val updatedList = _uiState.value.items.filterNot { it.id == itemId }
        _uiState.update {
            it.copy(
                items = updatedList,
                total = calculateTotal(updatedList, deliveryFee)
            )
        }
    }

    fun applyDeliveryFee(zipCode: String) {
        deliveryFee = if (zipCode.startsWith("01") || zipCode.startsWith("04")) 5.0 else 10.0
        _uiState.update {
            it.copy(total = calculateTotal(it.items, deliveryFee))
        }
    }

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
