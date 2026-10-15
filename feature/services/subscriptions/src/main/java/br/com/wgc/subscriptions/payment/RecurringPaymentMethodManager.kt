package br.com.wgc.subscriptions.payment

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cartão tokenizado cadastrado pelo usuário para cobrança recorrente de assinaturas.
 *
 * @property cardToken Token criptográfico fornecido pelo gateway PCI-DSS compliant.
 * @property lastFourDigits Últimos 4 dígitos para exibição ao cliente (ex: "4321").
 * @property brand Bandeira do cartão (ex: "MASTERCARD", "VISA", "ELO").
 * @property cardholderName Nome gravado no cartão.
 * @property expiryMonth Mês de validade (1 a 12).
 * @property expiryYear Ano de validade com 4 dígitos (ex: 2028).
 * @property isPrimary Indica se este é o cartão padrão debitado nas renovações automáticas.
 */
data class RecurringCard(
    val cardToken: String,
    val lastFourDigits: String,
    val brand: String,
    val cardholderName: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val isPrimary: Boolean = false
)

/**
 * Gerenciador corporativo de Métodos de Pagamento Recorrentes para Assinaturas.
 *
 * Permite cadastrar múltiplos cartões de crédito tokenizados, definir o cartão principal (default)
 * debitado na virada do ciclo de faturamento e manter cartões secundários de fallback para prevenção de churn involuntário.
 *
 * ### 📌 Quando Usar:
 * Utilize nas configurações de pagamento de assinaturas ([br.com.wgc.subscriptions.screen.SubscriptionsScreen])
 * para troca rápida de cartão e gerenciamento de cartões de backup.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val recurringManager = RecurringPaymentMethodManager(storage)
 *
 * // 1. Cadastrar novo cartão:
 * recurringManager.saveCard(cardNovo)
 *
 * // 2. Definir como cartão principal de renovação:
 * recurringManager.setPrimaryCard(cardNovo.cardToken)
 *
 * // 3. Obter cartão ativo de cobrança:
 * val cartaoPadrao = recurringManager.getPrimaryCard()
 * ```
 *
 * @param storage Armazenamento seguro de preferências persistentes do usuário.
 */
@Singleton
class RecurringPaymentMethodManager @Inject constructor(
    private val storage: KeyValueStorage
) {
    companion object {
        private const val KEY_RECURRING_CARDS = "wgc_recurring_cards_data"
        private const val CARD_DELIMITER = "###"
        private const val FIELD_DELIMITER = "@@@"
    }

    /**
     * Adiciona ou atualiza um cartão de crédito no repositório de cobrança recorrente.
     *
     * Se for o primeiro cartão adicionado, torna-se automaticamente o cartão principal.
     *
     * @param card Cartão tokenizado a ser cadastrado.
     */
    fun saveCard(card: RecurringCard) {
        val currentCards = getCards().toMutableList()
        currentCards.removeAll { it.cardToken == card.cardToken }

        val shouldBePrimary = card.isPrimary || currentCards.isEmpty()
        val adjustedCard = card.copy(isPrimary = shouldBePrimary)

        if (shouldBePrimary) {
            // Remove status de primário dos outros cartões
            for (i in currentCards.indices) {
                currentCards[i] = currentCards[i].copy(isPrimary = false)
            }
        }

        currentCards.add(0, adjustedCard)
        persistCards(currentCards)
    }

    /**
     * Retorna todos os cartões cadastrados pelo usuário.
     *
     * @return Lista de [RecurringCard] ativos.
     */
    fun getCards(): List<RecurringCard> {
        val serialized = storage.getString(KEY_RECURRING_CARDS, "") ?: ""
        if (serialized.isBlank()) return emptyList()

        return try {
            serialized.split(CARD_DELIMITER)
                .filter { it.isNotBlank() }
                .mapNotNull { deserializeCard(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Obtém o cartão principal configurado para as cobranças automáticas de renovação.
     *
     * @return [RecurringCard] primário ou o primeiro disponível, ou null se não houver registros.
     */
    fun getPrimaryCard(): RecurringCard? {
        val cards = getCards()
        return cards.find { it.isPrimary } ?: cards.firstOrNull()
    }

    /**
     * Alterna o cartão principal ativo para o token informado.
     *
     * @param cardToken Token do cartão a ser promovido como padrão.
     * @return true se o cartão foi localizado e promovido com sucesso.
     */
    fun setPrimaryCard(cardToken: String): Boolean {
        val currentCards = getCards().toMutableList()
        if (currentCards.none { it.cardToken == cardToken }) return false

        val updated = currentCards.map { card ->
            card.copy(isPrimary = card.cardToken == cardToken)
        }
        persistCards(updated)
        return true
    }

    /**
     * Remove um cartão cadastrado. Se o cartão removido for o principal, promove o próximo da lista.
     *
     * @param cardToken Token do cartão a ser deletado.
     * @return true se o cartão existia e foi removido.
     */
    fun removeCard(cardToken: String): Boolean {
        val currentCards = getCards().toMutableList()
        val removed = currentCards.removeIf { it.cardToken == cardToken }
        if (!removed) return false

        if (currentCards.isNotEmpty() && currentCards.none { it.isPrimary }) {
            currentCards[0] = currentCards[0].copy(isPrimary = true)
        }

        persistCards(currentCards)
        return true
    }

    /**
     * Remove todos os cartões cadastrados.
     */
    fun clearAllCards() {
        storage.remove(KEY_RECURRING_CARDS)
    }

    private fun persistCards(cards: List<RecurringCard>) {
        val serialized = cards.joinToString(CARD_DELIMITER) { card ->
            "${card.cardToken}$FIELD_DELIMITER${card.lastFourDigits}$FIELD_DELIMITER${card.brand}$FIELD_DELIMITER${card.cardholderName}$FIELD_DELIMITER${card.expiryMonth}$FIELD_DELIMITER${card.expiryYear}$FIELD_DELIMITER${card.isPrimary}"
        }
        storage.saveString(KEY_RECURRING_CARDS, serialized)
    }

    private fun deserializeCard(raw: String): RecurringCard? {
        val f = raw.split(FIELD_DELIMITER)
        if (f.size < 7) return null

        return RecurringCard(
            cardToken = f[0],
            lastFourDigits = f[1],
            brand = f[2],
            cardholderName = f[3],
            expiryMonth = f[4].toIntOrNull() ?: 1,
            expiryYear = f[5].toIntOrNull() ?: 2026,
            isPrimary = f[6].toBooleanStrictOrNull() ?: false
        )
    }
}
