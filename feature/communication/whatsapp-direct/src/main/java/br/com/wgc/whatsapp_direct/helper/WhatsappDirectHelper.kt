package br.com.wgc.whatsapp_direct.helper

import android.net.Uri
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Utilitário auxiliar para limpeza de números de telefone e criação de links profundos do WhatsApp.
 */
object WhatsappDirectHelper {

    /**
     * Higieniza o número telefônico removendo caracteres não numéricos e prefixando com o DDI brasileiro (55) se necessário.
     *
     * @param phone String de telefone com ou sem máscara.
     * @return String contendo apenas dígitos formatados com DDI.
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
     * Constrói a Uri oficial da API do WhatsApp com número e mensagem URL-encoded.
     *
     * @param phone Número do destinatário.
     * @param message Conteúdo textual a ser pré-preenchido no chat.
     * @return Objeto [Uri] configurado para a API oficial do WhatsApp.
     */
    fun createWhatsAppUri(phone: String, message: String): Uri {
        val clean = cleanPhoneNumber(phone)
        val encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8.name())
        return Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=$encodedMsg")
    }

    /**
     * Formata um pedido comercial estruturado com formatações nativas de negrito do WhatsApp.
     *
     * @param storeName Nome do estabelecimento destinatário.
     * @param customerName Nome do cliente que está realizando o pedido.
     * @param itemsSummary Resumo detalhado dos itens solicitados.
     * @param totalAmount Valor financeiro total do pedido.
     * @param deliveryAddress Local de entrega especificado pelo cliente.
     * @param paymentMethod Forma de pagamento acordada.
     * @param notes Observações adicionais ou instruções ao entregador.
     * @return String formatada com markdown compatível com WhatsApp.
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