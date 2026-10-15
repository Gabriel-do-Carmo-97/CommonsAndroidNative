package br.com.wgc.quotation.form

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Categorias de serviços especializadas suportadas no formulário de cotação.
 */
enum class QuotationCategory(val label: String) {
    /** Reformas, alvenaria, elétrica e reparos residenciais. */
    RESIDENTIAL_REPAIR("Reformas e Manutenção"),
    /** Marcenaria, móveis planejados e carpintaria sob medida. */
    CUSTOM_FURNITURE("Móveis Planejados"),
    /** Buffet, decoração e eventos corporativos/festas. */
    EVENTS_CATERING("Eventos e Gastronomia"),
    /** Projetos arquitetônicos, engenharia e design de interiores. */
    ARCHITECTURE("Projetos e Arquitetura"),
    /** Serviços de tecnologia, desenvolvimento de software e infraestrutura. */
    TECH_SERVICES("Serviços Digitais e TI")
}

/**
 * Rascunho das informações preenchidas pelo cliente nas etapas do formulário de orçamento.
 *
 * @property draftId Identificador único do rascunho (UUID).
 * @property category Categoria de serviço selecionada ([QuotationCategory]).
 * @property title Título conciso da necessidade (ex: "Instalação de Bancada em Granito").
 * @property description Detalhamento dos requisitos, dimensões e materiais pretendidos.
 * @property preferredDateIso Data prevista ou limite para o início dos trabalhos (ISO-8601).
 * @property estimatedBudgetMax Orçamento teto previsto pelo cliente (ou null se flexível).
 * @property attachedPhotoPaths Lista de caminhos de arquivos de fotos ou croquis anexados.
 * @property contactPhone Telefone ou WhatsApp para contato e retorno dos orçamentos.
 * @property currentStep Etapa atual do wizard multi-passos (1 a 3).
 */
data class QuotationDraft(
    val draftId: String,
    val category: QuotationCategory,
    val title: String = "",
    val description: String = "",
    val preferredDateIso: String = "",
    val estimatedBudgetMax: Double? = null,
    val attachedPhotoPaths: List<String> = emptyList(),
    val contactPhone: String = "",
    val currentStep: Int = 1
)

/**
 * Proposta consolidada de orçamento sob medida pronta para despacho aos fornecedores.
 *
 * @property quotationId Identificador oficial da cotação protocolada.
 * @property draft Rascunho validado com todas as informações compiladas.
 * @property submittedAt Carimbo de data/hora de envio em milissegundos.
 */
data class QuotationSubmission(
    val quotationId: String,
    val draft: QuotationDraft,
    val submittedAt: Long = System.currentTimeMillis()
)

/**
 * Resultado da validação de preenchimento de uma etapa do formulário.
 */
sealed interface QuotationValidationResult {
    /** Etapa preenchida com sucesso e em conformidade. */
    data object Valid : QuotationValidationResult

    /**
     * Erro de preenchimento identificado.
     *
     * @property step Etapa em que o erro ocorreu.
     * @property field Campo específico que falhou na validação.
     * @property message Mensagem orientativa em linguagem natural.
     */
    data class Invalid(val step: Int, val field: String, val message: String) : QuotationValidationResult
}

/**
 * Gerenciador corporativo do Formulário Dinâmico Multi-Etapas de Cotações Sob Medida.
 *
 * Conduz o fluxo wizard de 3 etapas para solicitação de orçamentos personalizados:
 * - Etapa 1: Escopo e Descrição dos Serviços.
 * - Etapa 2: Prazos, Datas Desejadas e Teto Orçamentário.
 * - Etapa 3: Upload de Fotos / Projetos e Dados de Contato.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de Cotações ([br.com.wgc.quotation.screen.QuotationScreen]) para controlar a navegação
 * entre os passos do formulário, validações pontuais e compilação do envio final.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val formManager = CustomQuotationFormManager()
 * var draft = formManager.createDraft(QuotationCategory.RESIDENTIAL_REPAIR)
 *
 * // Atualizar Passo 1:
 * draft = formManager.updateStep1(draft, title = "Troca de Fiação", description = "Quadro de energia...")
 * val step1Validation = formManager.validateStep(draft, step = 1)
 * ```
 */
@Singleton
class CustomQuotationFormManager @Inject constructor() {

    /**
     * Inicia um novo rascunho de cotação para a categoria indicada.
     *
     * @param category Categoria do serviço.
     * @return [QuotationDraft] inicializado na Etapa 1.
     */
    fun createDraft(category: QuotationCategory): QuotationDraft {
        return QuotationDraft(
            draftId = UUID.randomUUID().toString(),
            category = category,
            currentStep = 1
        )
    }

    /**
     * Atualiza os dados da Etapa 1 (Título e Descrição do Escopo).
     */
    fun updateStep1(draft: QuotationDraft, title: String, description: String): QuotationDraft {
        return draft.copy(
            title = title.trim(),
            description = description.trim(),
            currentStep = 1
        )
    }

    /**
     * Atualiza os dados da Etapa 2 (Datas e Expectativa de Orçamento).
     */
    fun updateStep2(draft: QuotationDraft, preferredDateIso: String, estimatedBudgetMax: Double?): QuotationDraft {
        return draft.copy(
            preferredDateIso = preferredDateIso.trim(),
            estimatedBudgetMax = estimatedBudgetMax,
            currentStep = 2
        )
    }

    /**
     * Atualiza os dados da Etapa 3 (Fotografias e Telefone de Retorno).
     */
    fun updateStep3(draft: QuotationDraft, photoPaths: List<String>, contactPhone: String): QuotationDraft {
        return draft.copy(
            attachedPhotoPaths = photoPaths,
            contactPhone = contactPhone.trim(),
            currentStep = 3
        )
    }

    /**
     * Valida os campos obrigatórios da etapa informada.
     *
     * @param draft Rascunho a ser auditado.
     * @param step Número da etapa (1, 2 ou 3).
     * @return [QuotationValidationResult.Valid] ou [QuotationValidationResult.Invalid].
     */
    fun validateStep(draft: QuotationDraft, step: Int): QuotationValidationResult {
        return when (step) {
            1 -> {
                if (draft.title.length < 5) {
                    QuotationValidationResult.Invalid(1, "title", "O título da cotação deve ter ao menos 5 caracteres.")
                } else if (draft.description.length < 15) {
                    QuotationValidationResult.Invalid(1, "description", "Descreva detalhadamente o serviço com pelo menos 15 caracteres.")
                } else {
                    QuotationValidationResult.Valid
                }
            }
            2 -> {
                if (draft.preferredDateIso.isBlank()) {
                    QuotationValidationResult.Invalid(2, "date", "Selecione uma data prevista para o início dos trabalhos.")
                } else if (draft.estimatedBudgetMax != null && draft.estimatedBudgetMax <= 0.0) {
                    QuotationValidationResult.Invalid(2, "budget", "O orçamento estimado deve ser um valor positivo.")
                } else {
                    QuotationValidationResult.Valid
                }
            }
            3 -> {
                val cleanPhone = draft.contactPhone.filter { it.isDigit() }
                if (cleanPhone.length < 10) {
                    QuotationValidationResult.Invalid(3, "phone", "Informe um telefone com DDD válido para contato.")
                } else {
                    QuotationValidationResult.Valid
                }
            }
            else -> QuotationValidationResult.Valid
        }
    }

    /**
     * Compila o rascunho completo em uma proposta oficial de cotação.
     *
     * @param draft Rascunho preenchido.
     * @return [QuotationSubmission] consolidado com protocolo oficial.
     * @throws IllegalStateException se alguma das 3 etapas não satisfizer os critérios de validação.
     */
    fun compileFinalQuotation(draft: QuotationDraft): QuotationSubmission {
        for (s in 1..3) {
            val validation = validateStep(draft, s)
            if (validation is QuotationValidationResult.Invalid) {
                throw IllegalStateException("Etapa $s inválida: ${validation.message}")
            }
        }

        return QuotationSubmission(
            quotationId = "COT-${UUID.randomUUID().toString().take(8).uppercase()}",
            draft = draft
        )
    }
}
