package br.com.wgc.whatsapp_direct.helper

import android.net.Uri
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WhatsappDirectHelper {

    fun cleanPhoneNumber(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return if (digits.length in 10..11 && !digits.startsWith("55")) {
            "55$digits"
        } else {
            digits
        }
    }

    fun createWhatsAppUri(phone: String, message: String): Uri {
        val clean = cleanPhoneNumber(phone)
        val encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8.name())
        return Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=$encodedMsg")
    }

    fun formatDeliveryOrderMessage(
        storeName: String,
        customerName: String,
        itemsSummary: String,
        totalAmount: String,
        deliveryAddress: String,
        paymentMethod: String,
        notes: String = ""
    ): String {
        return """
            🛍️ *NOVO PEDIDO - $storeName*
            Cliente: *$customerName*
            
            *Itens:*
            $itemsSummary
            
            *Valor Total:* *$totalAmount*
            *Entrega em:* $deliveryAddress
            *Pagamento:* $paymentMethod
            ${if (notes.isNotBlank()) "*Obs:* $notes\n" else ""}
            _Enviado via WGC Commons App_
        """.trimIndent()
    }
}
