package br.com.wgc.offline_sync.conflict

import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estratégia de resolução de conflito de dados sincronizados.
 */
enum class ConflictResolutionStrategy {
    /**
     * O registro com timestamp mais recente sobrescreve o mais antigo.
     */
    LAST_WRITE_WINS,

    /**
     * A versão local do cliente tem prioridade incondicional sobre a versão remota.
     */
    CLIENT_PRECEDENCE,

    /**
     * A versão do servidor remoto descarta as alterações locais pendentes.
     */
    SERVER_PRECEDENCE,

    /**
     * Mesclagem granular campo a campo; campos com conflito direto adotam o mais recente.
     */
    FIELD_LEVEL_MERGE
}

/**
 * Representação de uma entidade envolvida em conciliação de conflito de concorrência.
 *
 * @property entityId Identificador único do registro no banco local e remoto.
 * @property payloadJson Representação JSON dos dados da entidade.
 * @property updatedAtMillis Timestamp epoch em milissegundos da última alteração.
 * @property version Versão sequencial de versionamento otimista (ex: ETag / revision count).
 */
data class SyncEntityRecord(
    val entityId: String,
    val payloadJson: String,
    val updatedAtMillis: Long,
    val version: Long = 1L
)

/**
 * Resultado da conciliação de conflito de dados.
 *
 * @property resolvedPayloadJson JSON final consolidado pronto para persistência.
 * @property winningSource Origem que prevaleceu ("CLIENT", "SERVER", "MERGED").
 * @property finalVersion Versão final incrementada para a entidade.
 */
data class ConflictResolutionResult(
    val resolvedPayloadJson: String,
    val winningSource: String,
    val finalVersion: Long
)

/**
 * Motor enterprise de resolução e conciliação de conflitos de concorrência de dados offline.
 *
 * Fornece estratégias configuráveis para sincronização bidirecional entre o armazenamento
 * local (`Room` / `KeyValueStorage`) e os serviços remotos do `OmniBackend`.
 *
 * ### 📌 Quando Usar:
 * - Em operações de sincronização offline onde o registro no servidor sofreu mutações concorrentes
 *   enquanto o usuário realizava alterações em modo offline ou com instabilidade de rede.
 * - Para conciliação otimista de status de pedidos, edição de perfil, rascunhos de carrinho e notas de serviço.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val resolver = DataConflictResolver()
 * val clientRecord = SyncEntityRecord("order_1", "{\"status\":\"CONFIRMED\",\"note\":\"Entregar na portaria\"}", 1000L, 2)
 * val serverRecord = SyncEntityRecord("order_1", "{\"status\":\"PREPARING\",\"driverId\":\"drv_9\"}", 1050L, 2)
 *
 * val result = resolver.resolve(
 *     clientRecord = clientRecord,
 *     serverRecord = serverRecord,
 *     strategy = ConflictResolutionStrategy.FIELD_LEVEL_MERGE
 * )
 * println(result.resolvedPayloadJson)
 * ```
 */
@Singleton
class DataConflictResolver @Inject constructor() {

    /**
     * Resolve um conflito entre o registro local do cliente e o registro do servidor com base na estratégia informada.
     *
     * @param clientRecord Registro que foi alterado localmente pelo dispositivo.
     * @param serverRecord Registro atualmente persistido no OmniBackend remoto.
     * @param strategy Estratégia de resolução de conflitos a ser aplicada.
     * @return [ConflictResolutionResult] contendo os dados resolvidos e a versão atualizada.
     */
    fun resolve(
        clientRecord: SyncEntityRecord,
        serverRecord: SyncEntityRecord,
        strategy: ConflictResolutionStrategy = ConflictResolutionStrategy.LAST_WRITE_WINS
    ): ConflictResolutionResult {
        return when (strategy) {
            ConflictResolutionStrategy.CLIENT_PRECEDENCE -> {
                ConflictResolutionResult(
                    resolvedPayloadJson = clientRecord.payloadJson,
                    winningSource = "CLIENT",
                    finalVersion = maxOf(clientRecord.version, serverRecord.version) + 1L
                )
            }
            ConflictResolutionStrategy.SERVER_PRECEDENCE -> {
                ConflictResolutionResult(
                    resolvedPayloadJson = serverRecord.payloadJson,
                    winningSource = "SERVER",
                    finalVersion = serverRecord.version
                )
            }
            ConflictResolutionStrategy.LAST_WRITE_WINS -> {
                if (clientRecord.updatedAtMillis >= serverRecord.updatedAtMillis) {
                    ConflictResolutionResult(
                        resolvedPayloadJson = clientRecord.payloadJson,
                        winningSource = "CLIENT",
                        finalVersion = maxOf(clientRecord.version, serverRecord.version) + 1L
                    )
                } else {
                    ConflictResolutionResult(
                        resolvedPayloadJson = serverRecord.payloadJson,
                        winningSource = "SERVER",
                        finalVersion = serverRecord.version
                    )
                }
            }
            ConflictResolutionStrategy.FIELD_LEVEL_MERGE -> {
                val mergedJson = mergePayloads(clientRecord, serverRecord)
                ConflictResolutionResult(
                    resolvedPayloadJson = mergedJson,
                    winningSource = "MERGED",
                    finalVersion = maxOf(clientRecord.version, serverRecord.version) + 1L
                )
            }
        }
    }

    private fun mergePayloads(
        clientRecord: SyncEntityRecord,
        serverRecord: SyncEntityRecord
    ): String {
        return try {
            val serverObj = JSONObject(serverRecord.payloadJson)
            val clientObj = JSONObject(clientRecord.payloadJson)
            val mergedObj = JSONObject()

            val allKeys = mutableSetOf<String>()
            serverObj.keys().forEach { allKeys.add(it) }
            clientObj.keys().forEach { allKeys.add(it) }

            val clientIsNewer = clientRecord.updatedAtMillis >= serverRecord.updatedAtMillis

            for (key in allKeys) {
                val inServer = serverObj.has(key)
                val inClient = clientObj.has(key)

                when {
                    inServer && inClient -> {
                        // Conflito no mesmo campo: ganha quem for mais recente
                        val chosen = if (clientIsNewer) clientObj.get(key) else serverObj.get(key)
                        mergedObj.put(key, chosen)
                    }
                    inClient -> mergedObj.put(key, clientObj.get(key))
                    inServer -> mergedObj.put(key, serverObj.get(key))
                }
            }
            mergedObj.toString()
        } catch (_: Exception) {
            // Em caso de falha de parsing do JSON, faz fallback seguro para Last-Write-Wins
            if (clientRecord.updatedAtMillis >= serverRecord.updatedAtMillis) {
                clientRecord.payloadJson
            } else {
                serverRecord.payloadJson
            }
        }
    }
}
