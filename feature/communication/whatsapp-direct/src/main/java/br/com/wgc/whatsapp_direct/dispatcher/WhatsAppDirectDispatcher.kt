package br.com.wgc.whatsapp_direct.dispatcher

import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Contexto comercial de negócio para transbordo dinâmico ao WhatsApp.
 */
enum class WhatsAppMessageContext {
    /** Suporte e dúvidas operacionais sobre um pedido específico. */
    ORDER_SUPPORT,
    /** Confirmação e alinhamento sobre um agendamento de serviço. */
    SERVICE_BOOKING,
    /** Negociação e envio de especificações de cotação sob medida. */
    CUSTOM_QUOTATION,
    /** Contato geral do lojista ou suporte da plataforma. */
    GENERAL_INQUIRY
}

/**
 * Especificação corporativa da Intent e URI de acionamento do WhatsApp.
 *
 * @property uriString URI deep link pronta para parse pelo Android (`https://wa.me/...`).
 * @property targetPhone Número de telefone formatado com código de país DDI 55.
 * @property messageText Mensagem pré-formatada sem codificação percentual.
 * @property action Ação da Intent Android (padrão: `android.intent.action.VIEW`).
 * @property targetPackage Pacote do WhatsApp Android (`com.whatsapp`).
 */
data class WhatsAppIntentSpec(
    val uriString: String,
    val targetPhone: String,
    val messageText: String,
    val action: String = "android.intent.action.VIEW",
    val targetPackage: String = "com.whatsapp"
)

/**
 * Despachante corporativo de Transbordo Dinâmico para WhatsApp Comercial.
 *
 * Constrói deep links otimizados e mensagens estruturadas personalizadas por contexto
 * (pedidos, agendamentos, cotações), permitindo ao cliente iniciar conversa com o prestador
 * ou lojista com todo o contexto do pedido já preenchido.
 *
 * ### 📌 Quando Usar:
 * Acione no botão "Falar com o Lojista via WhatsApp" presente na tela de detalhes do pedido,
 * na visualização de prestador de serviços ou na confirmação de cotação.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val dispatcher = WhatsAppDirectDispatcher()
 * val spec = dispatcher.buildOrderSupportSpec(
 *     merchantPhone = "11987654321",
 *     orderId = "#WGC-1029",
 *     customerName = "Gabriel",
 *     status = "Saiu para Entrega"
 * )
 *
 * val intent = Intent(spec.action, Uri.parse(spec.uriString)).apply {
 *     setPackage(spec.targetPackage)
 * }
 * context.startActivity(intent)
 * ```
 */
@Singleton
class WhatsAppDirectDispatcher @Inject constructor() {

    companion object {
        private const val DEFAULT_COUNTRY_CODE = "55"
    }

    /**
     * Constrói a especificação de transbordo para suporte a um pedido específico.
     *
     * @param merchantPhone Número do lojista ou central de atendimento.
     * @param orderId Código visível do pedido.
     * @param customerName Nome do cliente solicitante.
     * @param status Status operacional atual da entrega.
     * @return [WhatsAppIntentSpec] configurado.
     */
    fun buildOrderSupportSpec(
        merchantPhone: String,
        orderId: String,
        customerName: String,
        status: String
    ): WhatsAppIntentSpec {
        val message = "Olá! Me chamo $customerName e gostaria de suporte sobre o pedido $orderId (Status: $status)."
        return createSpec(merchantPhone, message)
    }

    /**
     * Constrói a especificação de transbordo para dúvidas sobre um agendamento.
     *
     * @param professionalPhone Número do profissional.
     * @param professionalName Nome do prestador.
     * @param serviceName Procedimento/serviço agendado.
     * @param slotDateTime Data e horário do atendimento.
     * @return [WhatsAppIntentSpec] configurado.
     */
    fun buildServiceBookingSpec(
        professionalPhone: String,
        professionalName: String,
        serviceName: String,
        slotDateTime: String
    ): WhatsAppIntentSpec {
        val message = "Olá, $professionalName! Gostaria de confirmar detalhes sobre o agendamento de $serviceName em $slotDateTime."
        return createSpec(professionalPhone, message)
    }

    /**
     * Constrói a especificação de transbordo para negociação de cotação personalizada.
     *
     * @param targetPhone Número do fornecedor.
     * @param quotationId Protocolo da cotação.
     * @param title Título do serviço sob medida.
     * @return [WhatsAppIntentSpec] configurado.
     */
    fun buildQuotationSpec(
        targetPhone: String,
        quotationId: String,
        title: String
    ): WhatsAppIntentSpec {
        val message = "Olá! Gostaria de tratar da cotação $quotationId sobre '$title' enviada pelo aplicativo WGC."
        return createSpec(targetPhone, message)
    }

    /**
     * Higieniza o número de telefone e gera a URL deep link com codificação UTF-8.
     *
     * @param rawPhone Número bruto com ou sem formatação, DDD e DDI.
     * @param rawMessage Mensagem textual em linguagem natural.
     * @return [WhatsAppIntentSpec] consolidado.
     */
    fun createSpec(rawPhone: String, rawMessage: String): WhatsAppIntentSpec {
        val digitsOnly = rawPhone.filter { it.isDigit() }
        val phoneWithDdi = if (digitsOnly.startsWith("55") && digitsOnly.length >= 12) {
            digitsOnly
        } else if (digitsOnly.length in 10..11) {
            "$DEFAULT_COUNTRY_CODE$digitsOnly"
        } else {
            digitsOnly
        }

        val encodedText = try {
            URLEncoder.encode(rawMessage, "UTF-8").replace("+", "%20")
        } catch (_: Exception) {
            rawMessage
        }

        val uri = "https://api.whatsapp.com/send?phone=$phoneWithDdi&text=$encodedText"

        return WhatsAppIntentSpec(
            uriString = uri,
            targetPhone = phoneWithDdi,
            messageText = rawMessage
        )
    }
}
