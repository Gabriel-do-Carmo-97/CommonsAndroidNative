package br.com.wgc.settings.privacy

import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [AccountErasureManager] validando pré-requisitos, validação de frase de segurança e limpeza de storage.
 */
class AccountErasureManagerTest {

    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var manager: AccountErasureManager

    @Before
    fun setup() {
        storage = InMemoryKeyValueStorage()
        manager = AccountErasureManager(storage)
    }

    @Test
    fun `checkErasurePreconditions blocks erasure when active orders exist`() {
        val result = manager.checkErasurePreconditions(activeOrdersCount = 2)
        assertTrue(result is ErasurePreconditionResult.BlockedByActiveOrders)
        assertEquals(2, (result as ErasurePreconditionResult.BlockedByActiveOrders).activeOrdersCount)
    }

    @Test
    fun `checkErasurePreconditions blocks erasure when debt exists`() {
        val result = manager.checkErasurePreconditions(activeOrdersCount = 0, pendingDebtAmount = 45.50)
        assertTrue(result is ErasurePreconditionResult.BlockedByOutstandingDebt)
    }

    @Test
    fun `requestPermanentErasure rejects if confirmation phrase does not match exactly`() {
        val preconditions = manager.checkErasurePreconditions(activeOrdersCount = 0)
        val outcome = manager.requestPermanentErasure(
            userId = "u-99",
            confirmationText = "excluir",
            preconditions = preconditions
        )

        assertTrue(outcome is AccountErasureOutcome.Rejected)
        assertTrue((outcome as AccountErasureOutcome.Rejected).reason.contains(AccountErasureManager.MANDATORY_CONFIRMATION_PHRASE))
    }

    @Test
    fun `requestPermanentErasure succeeds and generates protocol and cleans storage when valid`() {
        storage.saveString("auth_token", "jwt_secret_token")

        val preconditions = manager.checkErasurePreconditions(activeOrdersCount = 0)
        val outcome = manager.requestPermanentErasure(
            userId = "u-100",
            confirmationText = "EXCLUIR MINHA CONTA",
            preconditions = preconditions
        )

        assertTrue(outcome is AccountErasureOutcome.Success)
        val success = outcome as AccountErasureOutcome.Success
        assertEquals("u-100", success.userId)
        assertTrue(success.confirmationProtocol.startsWith("LGPD-DEL-"))

        // Storage was wiped
        assertEquals("", storage.getString("auth_token", ""))
    }
}
