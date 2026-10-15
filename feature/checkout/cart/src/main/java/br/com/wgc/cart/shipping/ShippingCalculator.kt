package br.com.wgc.cart.shipping

import br.com.wgc.core.formatters.toCurrencyFormatted
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estimativa detalhada de taxa de frete e prazo de entrega.
 *
 * @property fee Valor monetário da taxa de entrega em Reais.
 * @property feeFormatted Taxa de entrega formatada pelo padrão corporativo.
 * @property estimatedDeliveryTime Prazo estimado para entrega física do pedido.
 * @property isExpress Indica se a região é elegível para despacho imediato (mesmo dia / moto entregador).
 */
data class ShippingEstimate(
    val fee: Double,
    val feeFormatted: String,
    val estimatedDeliveryTime: String,
    val isExpress: Boolean
)

/**
 * Calculador corporativo de Frete, Regiões Tarifárias e Estimativa de Entrega baseada em CEP.
 *
 * Determina automaticamente a taxa de entrega e o prazo médio logístico a partir da faixa numérica
 * de Código de Endereçamento Postal (CEP) brasileiro, suportando regras de frete expresso para capitais.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de carrinho (:feature:checkout:cart) e fechamento de pedido ao selecionar ou alterar
 * o endereço de entrega, recalculando o total da compra em tempo real.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val calculator = ShippingCalculator()
 * val estimate = calculator.calculateShipping(cep = "01310-100")
 * println("Taxa: ${estimate.feeFormatted} - Prazo: ${estimate.estimatedDeliveryTime}")
 * ```
 */
@Singleton
class ShippingCalculator @Inject constructor() {

    /**
     * Calcula a estimativa de frete para o CEP especificado.
     *
     * @param cep Código postal com ou sem formatação (ex: "01310-100" ou "01310100").
     * @return [ShippingEstimate] contendo valor, formato amigável e prazo previsto.
     */
    fun calculateShipping(cep: String): ShippingEstimate {
        val cleanDigits = cep.filter { it.isDigit() }

        if (cleanDigits.length != 8) {
            return ShippingEstimate(
                fee = 15.00,
                feeFormatted = 15.00.toCurrencyFormatted(),
                estimatedDeliveryTime = "2 a 4 dias úteis",
                isExpress = false
            )
        }

        val cepNum = cleanDigits.toIntOrNull() ?: 0

        return when {
            // Faixa SP Capital e Região Metropolitana (01000-000 a 09999-999)
            cepNum in 1000000..9999999 -> {
                ShippingEstimate(
                    fee = 7.90,
                    feeFormatted = 7.90.toCurrencyFormatted(),
                    estimatedDeliveryTime = "35 a 55 minutos",
                    isExpress = true
                )
            }
            // Faixa SP Litoral e Interior (11000-000 a 19999-999)
            cepNum in 11000000..19999999 -> {
                ShippingEstimate(
                    fee = 14.90,
                    feeFormatted = 14.90.toCurrencyFormatted(),
                    estimatedDeliveryTime = "1 a 2 dias úteis",
                    isExpress = false
                )
            }
            // Faixa Rio de Janeiro, Minas Gerais e Espírito Santo (20000-000 a 39999-999)
            cepNum in 20000000..39999999 -> {
                ShippingEstimate(
                    fee = 19.90,
                    feeFormatted = 19.90.toCurrencyFormatted(),
                    estimatedDeliveryTime = "2 a 3 dias úteis",
                    isExpress = false
                )
            }
            // Faixa Sul e Centro-Oeste (70000-000 a 99999-999)
            cepNum in 70000000..99999999 -> {
                ShippingEstimate(
                    fee = 24.90,
                    feeFormatted = 24.90.toCurrencyFormatted(),
                    estimatedDeliveryTime = "3 a 5 dias úteis",
                    isExpress = false
                )
            }
            // Demais regiões (Norte e Nordeste)
            else -> {
                ShippingEstimate(
                    fee = 32.90,
                    feeFormatted = 32.90.toCurrencyFormatted(),
                    estimatedDeliveryTime = "5 a 8 dias úteis",
                    isExpress = false
                )
            }
        }
    }
}
