package br.com.wgc.cart.domain.usecase

import javax.inject.Inject

/**
 * Item individual submetido para validação de checkout.
 *
 * @property id Identificador único do produto.
 * @property title Nome comercial do item.
 * @property quantity Quantidade selecionada pelo usuário.
 * @property unitPrice Preço unitário do item em Reais.
 */
data class CheckoutItem(
    val id: String,
    val title: String,
    val quantity: Int,
    val unitPrice: Double
)

/**
 * Requisição de validação pré-checkout consolidada.
 *
 * @property items Lista de itens presentes no carrinho.
 * @property addressId Identificador do endereço de entrega selecionado (opcional para retirada).
 * @property isDelivery Indica se o pedido exige modalidade de entrega em domicílio.
 */
data class CheckoutValidationRequest(
    val items: List<CheckoutItem>,
    val addressId: String? = null,
    val isDelivery: Boolean = true
)

/**
 * Resultado da validação das regras de negócio de checkout.
 */
sealed class CheckoutValidationResult {
    /** Carrinho validado e apto para seguir para pagamento. */
    data object Valid : CheckoutValidationResult()

    /**
     * Carrinho possui violações de regras de negócio.
     *
     * @property errorMessage Mensagem amigável com a causa da rejeição.
     */
    data class Invalid(val errorMessage: String) : CheckoutValidationResult()
}

/**
 * Caso de uso corporativo para validação estrita de integridade prévia ao fechamento do pedido.
 *
 * Garante que o carrinho não esteja vazio, que as quantidades e preços sejam estritamente positivos
 * e que pedidos com frete possuam endereço válido cadastrado.
 *
 * ### 📌 Quando Usar:
 * - Antes de transitar do carrinho (`:feature:checkout:cart`) para a seleção de forma de pagamento (`:feature:checkout:payment`).
 * - Ao confirmar a finalização do pedido de compras ou delivery.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val validator = ValidateCartCheckoutUseCase()
 * val request = CheckoutValidationRequest(
 *     items = listOf(CheckoutItem("p1", "Smartphone", 1, 1500.0)),
 *     addressId = "addr_1",
 *     isDelivery = true
 * )
 * val result = validator(request)
 * if (result is CheckoutValidationResult.Valid) {
 *     // navegar para pagamento
 * }
 * ```
 */
class ValidateCartCheckoutUseCase @Inject constructor() {

    /**
     * Executa a validação das regras de integridade do checkout.
     *
     * @param request Dados do carrinho e parâmetros de entrega.
     * @return [CheckoutValidationResult] indicando sucesso ou a mensagem do erro correspondente.
     */
    operator fun invoke(request: CheckoutValidationRequest): CheckoutValidationResult {
        if (request.items.isEmpty()) {
            return CheckoutValidationResult.Invalid("O carrinho não pode estar vazio para fechar o pedido.")
        }

        for (item in request.items) {
            if (item.quantity <= 0) {
                return CheckoutValidationResult.Invalid("O item \"${item.title}\" possui quantidade inválida.")
            }
            if (item.unitPrice < 0.0) {
                return CheckoutValidationResult.Invalid("O item \"${item.title}\" possui valor negativo inválido.")
            }
        }

        if (request.isDelivery && request.addressId.isNullOrBlank()) {
            return CheckoutValidationResult.Invalid("Selecione um endereço de entrega para pedidos com delivery.")
        }

        return CheckoutValidationResult.Valid
    }
}
