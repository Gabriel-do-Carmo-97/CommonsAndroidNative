package br.com.wgc.whatsapp_direct.helper

import android.net.Uri
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * UtilitÃ¡rio auxiliar para limpeza de nÃºmeros de telefone e criaÃ§Ã£o de links profundos do WhatsApp.
 */
object WhatsappDirectHelper {

    /**
     * Higieniza o nÃºmero telefÃ´nico removendo caracteres nÃ£o numÃ©ricos e prefixando com o DDI brasileiro (55) se necessÃ¡rio.
     *
     * @param phone String de telefone com ou sem mÃ¡scara.
     * @return String contendo apenas dÃ­gitos formatados com DDI.
     */
    fun cleanPhoneNumber(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return if (digits.length in 10..11 && !digits.startsWith("55")) {
            "55$digits"
        } else {
            digits
        }
    }

    /**
     * ConstrÃ³i a Uri oficial da API do WhatsApp com nÃºmero e mensagem URL-encoded.
     *
     * @param phone NÃºmero do destinatÃ¡rio.
     * @param message ConteÃºdo textual a ser prÃ©-preenchido no chat.
     * @return Objeto [Uri] configurado para a API oficial do WhatsApp.
     */
    fun createWhatsAppUri(phone: String, message: String): Uri {
        val clean = cleanPhoneNumber(phone)
        val encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8.name())
        return Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=$encodedMsg")
    }

    /**
     * Formata um pedido comercial estruturado com formataÃ§Ãµes nativas de negrito do WhatsApp.
     *
     * @param storeName Nome do estabelecimento destinatÃ¡rio.
     * @param customerName Nome do cliente que estÃ¡ realizando o pedido.
     * @param itemsSummary Resumo detalhado dos itens solicitados.
     * @param totalAmount Valor financeiro total do pedido.
     * @param deliveryAddress Local de entrega especificado pelo cliente.
     * @param paymentMethod Forma de pagamento acordada.
     * @param notes ObservaÃ§Ãµes adicionais ou instruÃ§Ãµes ao entregador.
     * @return String formatada com markdown compatÃ­vel com WhatsApp.
     */
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
            ðŸ›ï¸ *NOVO PEDIDO - $storeName*
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