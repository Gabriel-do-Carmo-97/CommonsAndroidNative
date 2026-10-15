package br.com.wgc.authentication.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [BruteForceProtectionManager] validando contadores de falhas e suspensões progressivas.
 */
class BruteForceProtectionManagerTest {

    private lateinit var manager: BruteForceProtectionManager

    @Before
    fun setup() {
        manager = BruteForceProtectionManager()
    }

    @Test
    fun `initial attempt is allowed with 3 attempts remaining`() {
        val check = manager.checkLockout("user@test.com")
        assertTrue(check is LockoutCheckResult.Allowed)
        assertEquals(0, (check as LockoutCheckResult.Allowed).consecutiveFailures)
        assertEquals(3, check.attemptsUntilNextLockout)
    }

    @Test
    fun `third consecutive failure triggers 30 seconds lockout`() {
        val t0 = 1000000L
        manager.recordFailedAttempt("user@test.com", t0)
        manager.recordFailedAttempt("user@test.com", t0 + 1000L)
        val thirdResult = manager.recordFailedAttempt("user@test.com", t0 + 2000L)

        assertTrue(thirdResult is LockoutCheckResult.Locked)
        val locked = thirdResult as LockoutCheckResult.Locked
        assertEquals(30, locked.remainingLockoutSeconds)
        assertEquals(3, locked.totalFailures)

        // Check during lockout
        val activeCheck = manager.checkLockout("user@test.com", currentTimeMillis = t0 + 10000L)
        assertTrue(activeCheck is LockoutCheckResult.Locked)

        // Check after 30s elapsed
        val afterLockout = manager.checkLockout("user@test.com", currentTimeMillis = t0 + 35000L)
        assertTrue(afterLockout is LockoutCheckResult.Allowed)
    }

    @Test
    fun `fifth consecutive failure triggers 5 minutes lockout`() {
        val t0 = 1000000L
        repeat(4) { manager.recordFailedAttempt("user2@test.com", t0) }
        val fifthResult = manager.recordFailedAttempt("user2@test.com", t0 + 1000L)

        assertTrue(fifthResult is LockoutCheckResult.Locked)
        val locked = fifthResult as LockoutCheckResult.Locked
        assertEquals(300, locked.remainingLockoutSeconds) // 5 minutes
    }

    @Test
    fun `recordSuccessfulLogin clears failure count`() {
        manager.recordFailedAttempt("user3@test.com")
        manager.recordFailedAttempt("user3@test.com")

        manager.recordSuccessfulLogin("user3@test.com")

        val check = manager.checkLockout("user3@test.com")
        assertTrue(check is LockoutCheckResult.Allowed)
        assertEquals(0, (check as LockoutCheckResult.Allowed).consecutiveFailures)
    }
}
