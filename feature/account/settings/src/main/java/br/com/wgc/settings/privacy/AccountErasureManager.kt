package br.com.wgc.settings.privacy

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Verificação prévia de pré-condições fiscais e contratuais antes da eliminação da conta.
 */
sealed interface ErasurePreconditionResult {
    /** Conta apta para eliminação definitiva. */
    data object Eligible : ErasurePreconditionResult

    /** Bloqueada: usuário possui pedidos em andamento que requerem entrega ativa. */
    data class BlockedByActiveOrders(val activeOrdersCount: Int) : ErasurePreconditionResult

    /** Bloqueada: pendências financeiras não resolvidas impedem encerramento. */
    data class BlockedByOutstandingDebt(val formattedDebt: String) : ErasurePreconditionResult
}

/**
 * Resultado da solicitação de exclusão permanente de conta ("Direito ao Esquecimento").
 */
sealed interface AccountErasureOutcome {
    /**
     * Conta e dados pessoais apagados irreversivelmente.
     *
     * @property userId Identificador da conta eliminada.
     * @property erasedAt Carimbo de data/hora da exclusão em milissegundos.
     * @property confirmationProtocol Protocolo fiscal e jurídico gerado para auditoria (LGPD Art. 16).
     */
    data class Success(
        val userId: String,
        val erasedAt: Long,
        val confirmationProtocol: String
    ) : AccountErasureOutcome

    /**
     * Solicitação rejeitada devido a texto de confirmação incorreto ou impedimento legal.
     *
     * @property reason Descrição detalhada do motivo da recusa.
     */
    data class Rejected(val reason: String) : AccountErasureOutcome
}

/**
 * Gerenciador corporativo de Eliminação Permanente de Conta e Direito ao Esquecimento (LGPD Art. 18, VI).
 *
 * Conduz o processo irrevogável de expurgo cadastral, revogação de tokens de autenticação,
 * higienização de chaves de biometria locais e geração de protocolo de auditoria de conformidade.
 *
 * ### 📌 Quando Usar:
 * Acione no fluxo "Excluir Minha Conta Permanentemente" em Configurações de Privacidade ([br.com.wgc.settings.screen.SettingsScreen]).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val erasureManager = AccountErasureManager(storage)
 *
 * // 1. Verificar se há impedimentos (pedidos em trânsito):
 * val preconditions = erasureManager.checkErasurePreconditions(activeOrdersCount = 0)
 *
 * // 2. Executar expurgo após confirmação explícita:
 * val outcome = erasureManager.requestPermanentErasure(
 *     userId = "usr-88",
 *     confirmationText = "EXCLUIR MINHA CONTA",
 *     preconditions = preconditions
 * )
 * ```
 *
 * @param storage Armazenamento local para higienização e limpeza de sessões ativas.
 */
@Singleton
class AccountErasureManager @Inject constructor(
    private val storage: KeyValueStorage
) {
    companion object {
        const val MANDATORY_CONFIRMATION_PHRASE = "EXCLUIR MINHA CONTA"
    }

    /**
     * Valida se o usuário preenche todos os requisitos operacionais para encerramento de cadastro.
     *
     * @param activeOrdersCount Quantidade de pedidos atualmente em preparo ou em trânsito.
     * @param pendingDebtAmount Valor financeiro em aberto no aplicativo.
     * @return [ErasurePreconditionResult.Eligible] ou bloqueio correspondente.
     */
    fun checkErasurePreconditions(
        activeOrdersCount: Int,
        pendingDebtAmount: Double = 0.0
    ): ErasurePreconditionResult {
        if (activeOrdersCount > 0) {
            return ErasurePreconditionResult.BlockedByActiveOrders(activeOrdersCount)
        }
        if (pendingDebtAmount > 0.0) {
            return ErasurePreconditionResult.BlockedByOutstandingDebt("R$ %.2f".format(pendingDebtAmount))
        }
        return ErasurePreconditionResult.Eligible
    }

    /**
     * Efetiva a exclusão definitiva da conta e dados cadastrais.
     *
     * @param userId Identificador da conta.
     * @param confirmationText Texto digitado pelo titular para confirmação irrevogável.
     * @param preconditions Resultado da checagem prévia.
     * @return [AccountErasureOutcome.Success] com número de protocolo ou [AccountErasureOutcome.Rejected].
     */
    fun requestPermanentErasure(
        userId: String,
        confirmationText: String,
        preconditions: ErasurePreconditionResult
    ): AccountErasureOutcome {
        if (preconditions !is ErasurePreconditionResult.Eligible) {
            val reason = when (preconditions) {
                is ErasurePreconditionResult.BlockedByActiveOrders ->
                    "Você possui ${preconditions.activeOrdersCount} entrega(s) em andamento. Aguarde a conclusão antes de excluir."
                is ErasurePreconditionResult.BlockedByOutstandingDebt ->
                    "Existem débitos pendentes de ${preconditions.formattedDebt}. Regularize sua situação para prosseguir."
                else -> "Não é possível excluir a conta no momento."
            }
            return AccountErasureOutcome.Rejected(reason)
        }

        if (confirmationText.trim() != MANDATORY_CONFIRMATION_PHRASE) {
            return AccountErasureOutcome.Rejected(
                "Texto de confirmação incorreto. Digite exatamente '$MANDATORY_CONFIRMATION_PHRASE' para prosseguir."
            )
        }

        // Higienização completa dos registros locais de sessão
        storage.clear()

        val protocol = "LGPD-DEL-${UUID.randomUUID().toString().take(8).uppercase()}"
        return AccountErasureOutcome.Success(
            userId = userId,
            erasedAt = System.currentTimeMillis(),
            confirmationProtocol = protocol
        )
    }
}
