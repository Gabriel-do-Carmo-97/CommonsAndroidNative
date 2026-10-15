package br.com.wgc.payment.googlepay

import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador corporativo para integração e orquestração do Google Pay (Google Wallet).
 *
 * Constrói as especificações JSON exigidas pela API oficial do Google Pay (`IsReadyToPayRequest` e
 * `PaymentDataRequest`), configurando tokenização via Gateway, redes de bandeiras aceitas e
 * parâmetros de moeda BRL (Real Brasileiro) em conformidade com as diretrizes do Google Play.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de checkout e pagamentos (:feature:checkout:payment) para exibir o botão oficial
 * "Pagar com GPay", agilizando a conversão de checkout em 1 clique sem digitação de cartão.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val gPayManager = GooglePayManager()
 * val isReadyJson = gPayManager.createIsReadyToPayRequest()
 *
 * // Criar payload para solicitação de pagamento de R$ 99,50:
 * val paymentDataJson = gPayManager.createPaymentDataRequest(totalPrice = "99.50")
 * ```
 */
@Singleton
class GooglePayManager @Inject constructor() {

    companion object {
        private const val GATEWAY_NAME = "wgc_enterprise_gateway"
        private const val GATEWAY_MERCHANT_ID = "wgc_merchant_001"
        private const val MERCHANT_NAME = "WGC Commons Store"
    }

    /**
     * Gera o objeto JSON para verificar se o dispositivo do usuário suporta e possui cartões aptos no Google Pay.
     *
     * @return String JSON formatada contendo a configuração de [IsReadyToPayRequest].
     */
    fun createIsReadyToPayRequest(): String {
        val baseRequest = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods", JSONArray().apply {
                put(getBaseCardPaymentMethod())
            })
        }
        return baseRequest.toString()
    }

    /**
     * Cria a requisição de pagamento detalhada contendo o valor total, moeda e tokenizador parceiro.
     *
     * @param totalPrice Valor monetário formatado com ponto decimal (ex: "45.00").
     * @param currencyCode Código internacional da moeda (padrão: "BRL").
     * @return String JSON formatada contendo a especificação [PaymentDataRequest].
     */
    fun createPaymentDataRequest(totalPrice: String, currencyCode: String = "BRL"): String {
        val cardPaymentMethod = getBaseCardPaymentMethod().apply {
            put("tokenizationSpecification", JSONObject().apply {
                put("type", "PAYMENT_GATEWAY")
                put("parameters", JSONObject().apply {
                    put("gateway", GATEWAY_NAME)
                    put("gatewayMerchantId", GATEWAY_MERCHANT_ID)
                })
            })
        }

        val transactionInfo = JSONObject().apply {
            put("totalPrice", totalPrice)
            put("totalPriceStatus", "FINAL")
            put("currencyCode", currencyCode)
        }

        val merchantInfo = JSONObject().apply {
            put("merchantName", MERCHANT_NAME)
        }

        val paymentDataRequest = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods", JSONArray().apply {
                put(cardPaymentMethod)
            })
            put("transactionInfo", transactionInfo)
            put("merchantInfo", merchantInfo)
        }

        return paymentDataRequest.toString()
    }

    /**
     * Extrai o token de pagamento da resposta retornada pelo Google Pay após aprovação biométrica do usuário.
     *
     * @param paymentDataJson JSON bruto retornado pela Activity de resultado do Google Pay.
     * @return Token criptográfico retornado pelo Gateway ou null caso inválido.
     */
    fun extractPaymentToken(paymentDataJson: String): String? {
        return try {
            val root = JSONObject(paymentDataJson)
            val paymentMethodData = root.getJSONObject("paymentMethodData")
            val tokenizationData = paymentMethodData.getJSONObject("tokenizationData")
            tokenizationData.getString("token")
        } catch (_: Exception) {
            null
        }
    }

    private fun getBaseCardPaymentMethod(): JSONObject {
        return JSONObject().apply {
            put("type", "CARD")
            put("parameters", JSONObject().apply {
                put("allowedAuthMethods", JSONArray().apply {
                    put("PAN_ONLY")
                    put("CRYPTOGRAM_3DS")
                })
                put("allowedCardNetworks", JSONArray().apply {
                    put("MASTERCARD")
                    put("VISA")
                    put("AMEX")
                    put("DISCOVER")
                })
                put("billingAddressRequired", false)
            })
        }
    }
}
