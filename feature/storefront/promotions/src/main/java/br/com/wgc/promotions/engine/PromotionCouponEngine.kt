package br.com.wgc.promotions.engine

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tipo ou modalidade de benefício concedido pelo cupom de desconto.
 */
enum class DiscountType {
    /** Desconto percentual aplicado sobre o valor dos produtos. */
    PERCENTAGE,

    /** Desconto de valor fixo subtraído do total. */
    FIXED_AMOUNT,

    /** Isenção total da taxa de frete ou entrega. */
    FREE_SHIPPING
}

/**
 * Modelo representativo de uma regra de cupom promocional corporativo.
 *
 * @property code Código alfanumérico do cupom (ex: "WGC10").
 * @property discountType Modalidade de benefício através de [DiscountType].
 * @property discountValue Valor numérico do benefício (percentual ou valor em reais).
 * @property minOrderValue Valor mínimo do pedido para habilitar o uso do cupom.
 * @property expiresAt Carimbo de expiração em milissegundos UTC.
 * @property maxUsesPerUser Quantidade máxima de resgates por usuário.
 */
data class PromotionCoupon(
    val code: String,
    val discountType: DiscountType,
    val discountValue: Double,
    val minOrderValue: Double = 0.0,
    val expiresAt: Long = Long.MAX_VALUE,
    val maxUsesPerUser: Int = 1
)

/**
 * Resultado da validação e cálculo de cupom promocional.
 */
sealed interface CouponValidationResult {
    /**
     * Cupom validado com sucesso e desconto calculado.
     *
     * @property coupon Dados do cupom aplicado.
     * @property discountAmount Valor monetário do desconto gerado.
     * @property finalTotal Valor final após dedução do desconto.
     * @property isFreeShippingGranted Indica se o frete foi zerado pelo cupom.
     */
    data class Valid(
        val coupon: PromotionCoupon,
        val discountAmount: Double,
        val finalTotal: Double,
        val isFreeShippingGranted: Boolean
    ) : CouponValidationResult

    /**
     * Sinaliza que o cupom não pôde ser aplicado.
     *
     * @property reason Motivo detalhado da recusa (ex: expirado, valor mínimo não atingido).
     */
    data class Invalid(val reason: String) : CouponValidationResult
}

/**
 * Motor corporativo de Cupons de Desconto, Promoções Progressivas e Regras de Vigência.
 *
 * Executa validação de regras de elegibilidade, cálculo de desconto progressivo e isenção de frete,
 * prevenindo fraudes de cupons e garantindo conformidade comercial antes do fechamento do pedido.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de carrinho (:feature:checkout:cart) ou durante o checkout (:feature:checkout:payment)
 * quando o usuário digitar um código de cupom ou selecionar uma oferta de Story promocional.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val engine = PromotionCouponEngine()
 * val result = engine.applyCoupon(
 *     couponCode = "WGC10",
 *     subtotal = 100.0,
 *     shippingFee = 10.0
 * )
 *
 * when (result) {
 *     is CouponValidationResult.Valid -> {
 *         println("Desconto: R$ ${result.discountAmount}, Total: R$ ${result.finalTotal}")
 *     }
 *     is CouponValidationResult.Invalid -> {
 *         println("Falha ao aplicar: ${result.reason}")
 *     }
 * }
 * ```
 */
@Singleton
class PromotionCouponEngine @Inject constructor() {

    private val registeredCoupons = mutableMapOf<String, PromotionCoupon>(
        "WGC10" to PromotionCoupon(
            code = "WGC10",
            discountType = DiscountType.PERCENTAGE,
            discountValue = 10.0,
            minOrderValue = 30.0
        ),
        "PRIMEIRACOMPRA" to PromotionCoupon(
            code = "PRIMEIRACOMPRA",
            discountType = DiscountType.FIXED_AMOUNT,
            discountValue = 20.0,
            minOrderValue = 60.0
        ),
        "FRETEGRATIS" to PromotionCoupon(
            code = "FRETEGRATIS",
            discountType = DiscountType.FREE_SHIPPING,
            discountValue = 0.0,
            minOrderValue = 40.0
        ),
        "VIP30" to PromotionCoupon(
            code = "VIP30",
            discountType = DiscountType.PERCENTAGE,
            discountValue = 30.0,
            minOrderValue = 100.0
        )
    )

    /**
     * Valida e calcula os valores de benefício para um cupom submetido.
     *
     * @param couponCode Código alfanumérico digitado pelo cliente.
     * @param subtotal Valor total dos produtos no carrinho.
     * @param shippingFee Taxa de entrega calculada para o endereço.
     * @param currentTimeMillis Carimbo de data/hora atual para validação de vigência (padrão: agora).
     * @return [CouponValidationResult] indicando sucesso com valores calculados ou falha com motivo.
     */
    fun applyCoupon(
        couponCode: String,
        subtotal: Double,
        shippingFee: Double,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): CouponValidationResult {
        val normalizedCode = couponCode.trim().uppercase()
        val coupon = registeredCoupons[normalizedCode]
            ?: return CouponValidationResult.Invalid("Cupom '$normalizedCode' não encontrado ou inválido.")

        if (currentTimeMillis > coupon.expiresAt) {
            return CouponValidationResult.Invalid("O cupom '$normalizedCode' está expirado.")
        }

        if (subtotal < coupon.minOrderValue) {
            val missing = coupon.minOrderValue - subtotal
            return CouponValidationResult.Invalid(
                "Valor mínimo para este cupom é R$ ${coupon.minOrderValue}. Adicione mais R$ %.2f em produtos.".format(missing)
            )
        }

        var discount = 0.0
        var isFreeShipping = false

        when (coupon.discountType) {
            DiscountType.PERCENTAGE -> {
                discount = subtotal * (coupon.discountValue / 100.0)
            }
            DiscountType.FIXED_AMOUNT -> {
                discount = coupon.discountValue.coerceAtMost(subtotal)
            }
            DiscountType.FREE_SHIPPING -> {
                isFreeShipping = true
            }
        }

        val effectiveShipping = if (isFreeShipping) 0.0 else shippingFee
        val finalTotal = (subtotal - discount + effectiveShipping).coerceAtLeast(0.0)

        return CouponValidationResult.Valid(
            coupon = coupon,
            discountAmount = discount,
            finalTotal = finalTotal,
            isFreeShippingGranted = isFreeShipping
        )
    }

    /**
     * Cadastra dinamicamente uma nova regra de cupom no motor corporativo.
     *
     * @param coupon Instância de [PromotionCoupon] a ser adicionada.
     */
    fun registerCoupon(coupon: PromotionCoupon) {
        registeredCoupons[coupon.code.trim().uppercase()] = coupon
    }
}
