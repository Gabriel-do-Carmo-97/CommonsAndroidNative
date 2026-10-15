package br.com.wgc.authentication.twofactor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [TwoFactorAuthManager] validando geração, validação, limite de tentativas e cooldown de reenvio.
 */
class TwoFactorAuthManagerTest {

    private lateinit var manager: TwoFactorAuthManager

    @Before
    fun setup() {
        manager = TwoFactorAuthManager()
    }

    @Test
    fun `requestChallenge creates valid 6-digit OTP and masked destination`() {
        val t0 = 1000000L
        val challenge = manager.requestChallenge(
            userId = "u-1",
            channel = TwoFactorDeliveryChannel.WHATSAPP,
            rawDestination = "11999991234",
            currentTimeMillis = t0
        )

        assertNotNull(challenge.challengeId)
        assertEquals(6, challenge.expectedCode.length)
        assertTrue(challenge.expectedCode.all { it.isDigit() })
        assertEquals("(**) *****-1234", challenge.destinationMasked)
        assertEquals(t0 + (5 * 60 * 1000L), challenge.expiresAtMillis)
        assertEquals(3, challenge.remainingAttempts)
    }

    @Test
    fun `verifyOtp with correct code returns Success`() {
        val challenge = manager.requestChallenge(
            userId = "u-2",
            channel = TwoFactorDeliveryChannel.SMS,
            rawDestination = "11888884321"
        )

        val outcome = manager.verifyOtp(challenge.challengeId, challenge.expectedCode)
        assertTrue(outcome is OtpVerificationOutcome.Success)
    }

    @Test
    fun `verifyOtp decrements remaining attempts and locks on third failure`() {
        val challenge = manager.requestChallenge(
            userId = "u-3",
            channel = TwoFactorDeliveryChannel.EMAIL,
            rawDestination = "gabriel@test.com"
        )

        // Attempt 1
        val outcome1 = manager.verifyOtp(challenge.challengeId, "000000")
        assertTrue(outcome1 is OtpVerificationOutcome.InvalidCode)
        assertEquals(2, (outcome1 as OtpVerificationOutcome.InvalidCode).attemptsRemaining)

        // Attempt 2
        val outcome2 = manager.verifyOtp(challenge.challengeId, "000000")
        assertTrue(outcome2 is OtpVerificationOutcome.InvalidCode)
        assertEquals(1, (outcome2 as OtpVerificationOutcome.InvalidCode).attemptsRemaining)

        // Attempt 3: Exceeded
        val outcome3 = manager.verifyOtp(challenge.challengeId, "000000")
        assertTrue(outcome3 is OtpVerificationOutcome.MaxAttemptsExceeded)
    }

    @Test
    fun `resendOtp respects 60s cooldown`() {
        val t0 = 1000000L
        val challenge = manager.requestChallenge(
            userId = "u-4",
            channel = TwoFactorDeliveryChannel.SMS,
            rawDestination = "11999990000",
            currentTimeMillis = t0
        )

        // Attempt resend at t0 + 20s (inside 60s cooldown)
        val cooldownOutcome = manager.resendOtp(challenge.challengeId, currentTimeMillis = t0 + 20000L)
        assertTrue(cooldownOutcome is OtpResendOutcome.CooldownActive)
        assertEquals(40, (cooldownOutcome as OtpResendOutcome.CooldownActive).remainingSeconds)

        // Attempt resend at t0 + 61s (after cooldown)
        val resendOutcome = manager.resendOtp(challenge.challengeId, currentTimeMillis = t0 + 61000L)
        assertTrue(resendOutcome is OtpResendOutcome.Resent)
    }
}
