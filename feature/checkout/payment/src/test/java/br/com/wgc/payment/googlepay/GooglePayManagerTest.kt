package br.com.wgc.payment.googlepay

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [GooglePayManager] validando geração de especificações JSON do Google Pay.
 */
class GooglePayManagerTest {

    private lateinit var gPayManager: GooglePayManager

    @Before
    fun setup() {
        gPayManager = GooglePayManager()
    }

    @Test
    fun `createIsReadyToPayRequest produces valid JSON with apiVersion 2`() {
        val jsonStr = gPayManager.createIsReadyToPayRequest()
        val json = JSONObject(jsonStr)

        assertEquals(2, json.getInt("apiVersion"))
        assertEquals(0, json.getInt("apiVersionMinor"))
        assertTrue(json.getJSONArray("allowedPaymentMethods").length() > 0)
    }

    @Test
    fun `createPaymentDataRequest includes transactionInfo with price and currency BRL`() {
        val jsonStr = gPayManager.createPaymentDataRequest(totalPrice = "120.50", currencyCode = "BRL")
        val json = JSONObject(jsonStr)

        val txInfo = json.getJSONObject("transactionInfo")
        assertEquals("120.50", txInfo.getString("totalPrice"))
        assertEquals("BRL", txInfo.getString("currencyCode"))
        assertEquals("FINAL", txInfo.getString("totalPriceStatus"))
    }

    @Test
    fun `extractPaymentToken parses token correctly from mock payload`() {
        val mockPayload = """
            {
                "paymentMethodData": {
                    "tokenizationData": {
                        "token": "example_google_pay_token_12345"
                    }
                }
            }
        """.trimIndent()

        val token = gPayManager.extractPaymentToken(mockPayload)
        assertNotNull(token)
        assertEquals("example_google_pay_token_12345", token)
    }
}
