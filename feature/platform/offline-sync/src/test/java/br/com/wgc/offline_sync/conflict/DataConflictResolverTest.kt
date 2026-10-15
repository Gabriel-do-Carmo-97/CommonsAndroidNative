package br.com.wgc.offline_sync.conflict

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para o motor [DataConflictResolver].
 */
class DataConflictResolverTest {

    private lateinit var resolver: DataConflictResolver

    @Before
    fun setup() {
        resolver = DataConflictResolver()
    }

    @Test
    fun `resolve com CLIENT_PRECEDENCE deve priorizar dados do cliente`() {
        val client = SyncEntityRecord("doc_1", "{\"title\":\"Client Title\"}", 100L, 1L)
        val server = SyncEntityRecord("doc_1", "{\"title\":\"Server Title\"}", 200L, 2L)

        val result = resolver.resolve(client, server, ConflictResolutionStrategy.CLIENT_PRECEDENCE)

        assertEquals("CLIENT", result.winningSource)
        assertEquals(client.payloadJson, result.resolvedPayloadJson)
        assertEquals(3L, result.finalVersion)
    }

    @Test
    fun `resolve com SERVER_PRECEDENCE deve priorizar dados do servidor`() {
        val client = SyncEntityRecord("doc_1", "{\"title\":\"Client Title\"}", 100L, 1L)
        val server = SyncEntityRecord("doc_1", "{\"title\":\"Server Title\"}", 200L, 2L)

        val result = resolver.resolve(client, server, ConflictResolutionStrategy.SERVER_PRECEDENCE)

        assertEquals("SERVER", result.winningSource)
        assertEquals(server.payloadJson, result.resolvedPayloadJson)
        assertEquals(2L, result.finalVersion)
    }

    @Test
    fun `resolve com LAST_WRITE_WINS deve priorizar cliente quando cliente for mais recente`() {
        val client = SyncEntityRecord("doc_1", "{\"title\":\"Client Newer\"}", 500L, 2L)
        val server = SyncEntityRecord("doc_1", "{\"title\":\"Server Older\"}", 300L, 2L)

        val result = resolver.resolve(client, server, ConflictResolutionStrategy.LAST_WRITE_WINS)

        assertEquals("CLIENT", result.winningSource)
        assertEquals(client.payloadJson, result.resolvedPayloadJson)
    }

    @Test
    fun `resolve com LAST_WRITE_WINS deve priorizar servidor quando servidor for mais recente`() {
        val client = SyncEntityRecord("doc_1", "{\"title\":\"Client Older\"}", 100L, 1L)
        val server = SyncEntityRecord("doc_1", "{\"title\":\"Server Newer\"}", 300L, 2L)

        val result = resolver.resolve(client, server, ConflictResolutionStrategy.LAST_WRITE_WINS)

        assertEquals("SERVER", result.winningSource)
        assertEquals(server.payloadJson, result.resolvedPayloadJson)
    }

    @Test
    fun `resolve com FIELD_LEVEL_MERGE deve mesclar campos disjuntos e conciliar conflito`() {
        val client = SyncEntityRecord(
            entityId = "profile_1",
            payloadJson = "{\"name\":\"Gabriel\",\"phone\":\"99999999\"}",
            updatedAtMillis = 2000L,
            version = 1L
        )
        val server = SyncEntityRecord(
            entityId = "profile_1",
            payloadJson = "{\"name\":\"Gabriel C.\",\"email\":\"dev@wgc.com.br\"}",
            updatedAtMillis = 1000L,
            version = 1L
        )

        val result = resolver.resolve(client, server, ConflictResolutionStrategy.FIELD_LEVEL_MERGE)

        assertEquals("MERGED", result.winningSource)
        val json = JSONObject(result.resolvedPayloadJson)
        // Conflito em "name": cliente é mais recente (2000 > 1000), então "Gabriel"
        assertEquals("Gabriel", json.getString("name"))
        // Campo exclusivo do cliente
        assertEquals("99999999", json.getString("phone"))
        // Campo exclusivo do servidor
        assertEquals("dev@wgc.com.br", json.getString("email"))
    }
}
