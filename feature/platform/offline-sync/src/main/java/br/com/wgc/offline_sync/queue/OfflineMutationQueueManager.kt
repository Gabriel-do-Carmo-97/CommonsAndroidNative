package br.com.wgc.offline_sync.queue

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.pow

/**
 * Operação transacional de mutação em dados realizada localmente sem conectividade.
 */
enum class MutationOperationType {
    /** Criação de um novo registro (INSERT / POST). */
    CREATE,
    /** Atualização de dados de uma entidade existente (UPDATE / PATCH). */
    UPDATE,
    /** Exclusão de registro (DELETE). */
    DELETE
}

/**
 * Estado de processamento da mutação na fila de sincronização.
 */
enum class MutationStatus {
    /** Pendente na fila aguardando conexão com a internet. */
    PENDING,
    /** Em trânsito sendo enviada ao OmniBackend Functions. */
    PROCESSING,
    /** Sincronizada com sucesso no banco de dados remoto. */
    COMPLETED,
    /** Falha permanente após esgotar tentativas com backoff exponencial. */
    FAILED
}

/**
 * Registro de mutação pendente serializado e preservado localmente no dispositivo.
 *
 * @property mutationId Identificador único do comando transacional (UUID).
 * @property entityType Coleção ou tabela de destino (ex: "orders", "cart", "user_profile").
 * @property entityId Identificador exclusivo da entidade modificada.
 * @property operation Tipo de operação executada ([MutationOperationType]).
 * @property payloadJson Dados da entidade serializados em JSON.
 * @property retryCount Quantidade de tentativas de envio já realizadas.
 * @property createdAtMillis Carimbo de criação da mutação offline.
 * @property lastAttemptMillis Carimbo da última tentativa de envio.
 * @property status Estado de resolução na fila ([MutationStatus]).
 */
data class OfflineMutationRecord(
    val mutationId: String,
    val entityType: String,
    val entityId: String,
    val operation: MutationOperationType,
    val payloadJson: String,
    val retryCount: Int = 0,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val lastAttemptMillis: Long = 0L,
    val status: MutationStatus = MutationStatus.PENDING
)

/**
 * Gerenciador corporativo da Fila de Sincronização Transacional Offline com Backoff Exponencial.
 *
 * Enfileira e persiste mutações de dados ocorridas quando o aparelho do usuário está sem rede,
 * garantindo idempotência e reprocessamento com política de espera exponencial (Exponential Backoff)
 * assim que o WorkManager detectar o restabelecimento da conectividade (`NetworkType.CONNECTED`).
 *
 * ### 📌 Quando Usar:
 * Acione no repositório de dados ou ViewModel (:feature:platform:offline-sync) sempre que uma operação
 * de salvar, editar ou excluir falhar por queda de sinal de rede móvel ou Wi-Fi.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val queueManager = OfflineMutationQueueManager(storage)
 *
 * // 1. Enfileirar mutação offline:
 * val mutation = queueManager.enqueueMutation(
 *     entityType = "orders",
 *     entityId = "ord-99",
 *     operation = MutationOperationType.CREATE,
 *     payloadJson = "{ ... }"
 * )
 *
 * // 2. No Worker de sincronização com internet ativa:
 * val pendentes = queueManager.getPendingMutations()
 * pendentes.forEach { item ->
 *     enviarParaOmniBackend(item)
 *     queueManager.markCompleted(item.mutationId)
 * }
 * ```
 *
 * @param storage Armazenamento persistente para a fila transacional.
 */
@Singleton
class OfflineMutationQueueManager @Inject constructor(
    private val storage: KeyValueStorage
) {
    companion object {
        private const val KEY_MUTATIONS = "wgc_offline_mutations_queue"
        private const val RECORD_DELIMITER = "###"
        private const val FIELD_DELIMITER = "@@@"
        const val MAX_RETRIES = 5
    }

    private val inMemoryQueue = mutableListOf<OfflineMutationRecord>()

    init {
        loadQueueFromStorage()
    }

    /**
     * Adiciona uma nova mutação à fila de sincronização persistente.
     *
     * @param entityType Nome da entidade (ex: "orders").
     * @param entityId ID da entidade.
     * @param operation Tipo de mutação.
     * @param payloadJson Payload JSON serializado.
     * @return [OfflineMutationRecord] gerado e enfileirado.
     */
    fun enqueueMutation(
        entityType: String,
        entityId: String,
        operation: MutationOperationType,
        payloadJson: String
    ): OfflineMutationRecord {
        val record = OfflineMutationRecord(
            mutationId = UUID.randomUUID().toString(),
            entityType = entityType,
            entityId = entityId,
            operation = operation,
            payloadJson = payloadJson,
            retryCount = 0,
            status = MutationStatus.PENDING
        )

        inMemoryQueue.add(record)
        persistQueue()
        return record
    }

    /**
     * Retorna a lista de mutações que aguardam envio ao backend.
     *
     * @return Lista de [OfflineMutationRecord] com status [MutationStatus.PENDING].
     */
    fun getPendingMutations(): List<OfflineMutationRecord> {
        return inMemoryQueue.filter { it.status == MutationStatus.PENDING }
    }

    /**
     * Marca uma mutação como concluída e a remove da fila de sincronização ativa.
     *
     * @param mutationId Identificador da mutação finalizada.
     */
    fun markCompleted(mutationId: String) {
        inMemoryQueue.removeAll { it.mutationId == mutationId }
        persistQueue()
    }

    /**
     * Registra uma falha na tentativa de envio, incrementando o contador de retentativas.
     * Se o limite de retentativas for atingido, marca como [MutationStatus.FAILED].
     *
     * @param mutationId Identificador da mutação.
     * @param currentTimeMillis Carimbo de data/hora da tentativa.
     * @return [OfflineMutationRecord] atualizado.
     */
    fun recordRetryFailure(
        mutationId: String,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): OfflineMutationRecord? {
        val index = inMemoryQueue.indexOfFirst { it.mutationId == mutationId }
        if (index == -1) return null

        val current = inMemoryQueue[index]
        val newRetryCount = current.retryCount + 1
        val newStatus = if (newRetryCount >= MAX_RETRIES) MutationStatus.FAILED else MutationStatus.PENDING

        val updated = current.copy(
            retryCount = newRetryCount,
            lastAttemptMillis = currentTimeMillis,
            status = newStatus
        )

        inMemoryQueue[index] = updated
        persistQueue()
        return updated
    }

    /**
     * Calcula o tempo de espera (delay) recomendado para a próxima tentativa utilizando backoff exponencial.
     *
     * Fórmula: `min(maxDelay, baseDelay * 2^retryCount)`.
     *
     * @param retryCount Quantidade de tentativas anteriores já falhas.
     * @param baseDelayMs Atraso base inicial em milissegundos (padrão: 5.000ms = 5s).
     * @param maxDelayMs Teto máximo de espera em milissegundos (padrão: 300.000ms = 5min).
     * @return Milissegundos a aguardar antes da próxima requisição de sincronização.
     */
    fun calculateExponentialBackoffDelayMs(
        retryCount: Int,
        baseDelayMs: Long = 5000L,
        maxDelayMs: Long = 300000L
    ): Long {
        if (retryCount <= 0) return 0L
        val multiplier = 2.0.pow((retryCount - 1).coerceAtMost(10).toDouble()).toLong()
        val calculated = baseDelayMs * multiplier
        return min(maxDelayMs, calculated)
    }

    /**
     * Limpa toda a fila de mutações.
     */
    fun clearQueue() {
        inMemoryQueue.clear()
        storage.remove(KEY_MUTATIONS)
    }

    private fun persistQueue() {
        val serialized = inMemoryQueue.joinToString(RECORD_DELIMITER) { record ->
            "${record.mutationId}$FIELD_DELIMITER${record.entityType}$FIELD_DELIMITER${record.entityId}$FIELD_DELIMITER${record.operation.name}$FIELD_DELIMITER${record.retryCount}$FIELD_DELIMITER${record.createdAtMillis}$FIELD_DELIMITER${record.lastAttemptMillis}$FIELD_DELIMITER${record.status.name}$FIELD_DELIMITER${record.payloadJson}"
        }
        storage.saveString(KEY_MUTATIONS, serialized)
    }

    private fun loadQueueFromStorage() {
        val raw = storage.getString(KEY_MUTATIONS, "") ?: ""
        if (raw.isBlank()) return

        inMemoryQueue.clear()
        raw.split(RECORD_DELIMITER).forEach { rec ->
            val f = rec.split(FIELD_DELIMITER)
            if (f.size >= 9) {
                val record = OfflineMutationRecord(
                    mutationId = f[0],
                    entityType = f[1],
                    entityId = f[2],
                    operation = try { MutationOperationType.valueOf(f[3]) } catch (_: Exception) { MutationOperationType.UPDATE },
                    retryCount = f[4].toIntOrNull() ?: 0,
                    createdAtMillis = f[5].toLongOrNull() ?: 0L,
                    lastAttemptMillis = f[6].toLongOrNull() ?: 0L,
                    status = try { MutationStatus.valueOf(f[7]) } catch (_: Exception) { MutationStatus.PENDING },
                    payloadJson = f[8]
                )
                inMemoryQueue.add(record)
            }
        }
    }
}
