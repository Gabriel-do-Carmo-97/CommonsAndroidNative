package br.com.wgc.subscriptions.receipt

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Item discriminado no comprovante fiscal de serviço ou transação de assinatura.
 *
 * @property description Descrição do serviço prestado ou plano faturado.
 * @property quantity Quantidade de unidades faturadas.
 * @property unitPriceFormatted Valor unitário formatado (ex: "R$ 49,90").
 * @property totalFormatted Valor total da linha (ex: "R$ 49,90").
 */
data class ReceiptItem(
    val description: String,
    val quantity: Int = 1,
    val unitPriceFormatted: String,
    val totalFormatted: String
)

/**
 * Documento consolidado de recibo de pagamento e comprovante fiscal de serviços.
 *
 * @property receiptNumber Código alfanumérico do recibo (ex: "REC-2026-98124").
 * @property customerName Nome completo do titular pagador.
 * @property customerTaxIdMasked CPF/CNPJ mascarado do titular (ex: "***.456.789-**").
 * @property providerName Razão social do prestador/emissor.
 * @property providerTaxId CNPJ oficial da empresa emissora.
 * @property issueDate Data e hora de emissão do comprovante.
 * @property items Lista de itens ou parcelas inclusas.
 * @property subtotalFormatted Subtotal dos serviços prestados.
 * @property discountFormatted Descontos aplicados (ex: "R$ 0,00").
 * @property totalFormatted Valor total efetivamente quitado.
 * @property paymentMethodDescription Forma de pagamento utilizada (ex: "Cartão de Crédito final 4321").
 * @property authorizationCode Código de autorização retornado pela adquirente/gateway bancário.
 */
data class ServiceReceiptDocument(
    val receiptNumber: String,
    val customerName: String,
    val customerTaxIdMasked: String,
    val providerName: String,
    val providerTaxId: String,
    val issueDate: String,
    val items: List<ReceiptItem>,
    val subtotalFormatted: String,
    val discountFormatted: String = "R$ 0,00",
    val totalFormatted: String,
    val paymentMethodDescription: String,
    val authorizationCode: String
)

/**
 * Especificação para compartilhamento nativo do comprovante via `Intent.ACTION_SEND`.
 *
 * @property action Ação da Intent Android (`android.intent.action.SEND`).
 * @property subject Assunto da mensagem de compartilhamento.
 * @property shareBody Conteúdo textual detalhado do recibo formatado.
 * @property mimeType Tipo MIME do documento (`text/plain` ou `application/pdf`).
 */
data class ReceiptShareSpec(
    val action: String = "android.intent.action.SEND",
    val subject: String,
    val shareBody: String,
    val mimeType: String = "text/plain"
)

/**
 * Gerador corporativo de Extratos, Comprovantes de Pagamento e Recibos Fiscais.
 *
 * Compila relatórios transacionais estruturados contendo dados fiscais, discriminação de serviços,
 * códigos de autorização bancária e botões para despacho direto via `Intent.ACTION_SEND` (WhatsApp, E-mail, Impressão).
 *
 * ### 📌 Quando Usar:
 * Acione ao clicar em "Baixar Comprovante" ou "Compartilhar Recibo" na tela de Assinaturas
 * ([br.com.wgc.subscriptions.screen.SubscriptionsScreen]) ou no Histórico Financeiro.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val receiptGenerator = ReceiptPdfGenerator()
 * val shareSpec = receiptGenerator.buildShareSpec(receiptDoc)
 *
 * val intent = Intent(shareSpec.action).apply {
 *     type = shareSpec.mimeType
 *     putExtra(Intent.EXTRA_SUBJECT, shareSpec.subject)
 *     putExtra(Intent.EXTRA_TEXT, shareSpec.shareBody)
 * }
 * context.startActivity(Intent.createChooser(intent, "Compartilhar Comprovante"))
 * ```
 */
@Singleton
class ReceiptPdfGenerator @Inject constructor() {

    /**
     * Formata um documento de recibo em layout estruturado de texto para visualização e envio rápido.
     *
     * @param document Dados do comprovante fiscal.
     * @return String formatada com delimitadores visuais, cabeçalho e rodapé fiscal.
     */
    fun formatReceiptText(document: ServiceReceiptDocument): String {
        val sb = StringBuilder()
        sb.appendLine("==================================================")
        sb.appendLine("             COMPROVANTE DE PAGAMENTO             ")
        sb.appendLine("==================================================")
        sb.appendLine("Recibo Nº: ${document.receiptNumber}")
        sb.appendLine("Data de Emissão: ${document.issueDate}")
        sb.appendLine("Emissor: ${document.providerName} (CNPJ: ${document.providerTaxId})")
        sb.appendLine("Cliente: ${document.customerName} (CPF: ${document.customerTaxIdMasked})")
        sb.appendLine("--------------------------------------------------")
        sb.appendLine("ITENS / SERVIÇOS DISCRIMINADOS:")

        document.items.forEach { item ->
            sb.appendLine("- ${item.description} (x${item.quantity}): ${item.totalFormatted}")
        }

        sb.appendLine("--------------------------------------------------")
        sb.appendLine("Subtotal:  ${document.subtotalFormatted}")
        sb.appendLine("Descontos: ${document.discountFormatted}")
        sb.appendLine("TOTAL:     ${document.totalFormatted}")
        sb.appendLine("Forma de Pagamento: ${document.paymentMethodDescription}")
        sb.appendLine("Código de Autenticação: ${document.authorizationCode}")
        sb.appendLine("==================================================")
        sb.appendLine("Documento para simples conferência. Emitido via WGC Commons.")
        return sb.toString()
    }

    /**
     * Constrói a especificação [ReceiptShareSpec] pronta para envio a outras aplicações do dispositivo.
     *
     * @param document Dados do comprovante fiscal.
     * @return [ReceiptShareSpec] configurada.
     */
    fun buildShareSpec(document: ServiceReceiptDocument): ReceiptShareSpec {
        val body = formatReceiptText(document)
        return ReceiptShareSpec(
            subject = "Comprovante de Pagamento - ${document.receiptNumber}",
            shareBody = body,
            mimeType = "text/plain"
        )
    }
}
