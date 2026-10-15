package br.com.wgc.subscriptions.lifecycle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [SubscriptionLifecycleManager] validando transições de ciclo de vida e banners informativos.
 */
class SubscriptionLifecycleManagerTest {

    private lateinit var manager: SubscriptionLifecycleManager

    @Before
    fun setup() {
        manager = SubscriptionLifecycleManager()
    }

    @Test
    fun `active subscription before end period shows no banner`() {
        val sub = UserSubscriptionState(
            subscriptionId = "sub-1",
            planId = "p1",
            planName = "Pro",
            status = SubscriptionStatus.ACTIVE,
            currentPeriodStartMillis = 1000L,
            currentPeriodEndMillis = 50000L
        )

        val banner = manager.generateBannerAlert(sub, currentTimeMillis = 25000L)
        assertNull(banner)
    }

    @Test
    fun `subscription past end period transitions to GRACE_PERIOD and shows warning banner`() {
        val tEnd = 1000000L
        val sub = UserSubscriptionState(
            subscriptionId = "sub-2",
            planId = "p1",
            planName = "Pro",
            status = SubscriptionStatus.ACTIVE,
            currentPeriodStartMillis = 0L,
            currentPeriodEndMillis = tEnd,
            autoRenew = true
        )

        // Checked 1 hour after period ended
        val banner = manager.generateBannerAlert(sub, currentTimeMillis = tEnd + (60 * 60 * 1000L))
        assertNotNull(banner)
        assertEquals("Falha na renovação da assinatura", banner?.title)
        assertTrue(banner?.message?.contains("período de tolerância") == false) // Still in grace
        assertEquals(false, banner?.isCritical)
    }

    @Test
    fun `subscription past grace period transitions to DELINQUENT and shows critical banner`() {
        val tEnd = 1000000L
        val graceEnd = tEnd + (3 * 24 * 60 * 60 * 1000L) // 3 days
        val sub = UserSubscriptionState(
            subscriptionId = "sub-3",
            planId = "p1",
            planName = "VIP",
            status = SubscriptionStatus.GRACE_PERIOD,
            currentPeriodStartMillis = 0L,
            currentPeriodEndMillis = tEnd,
            gracePeriodEndMillis = graceEnd
        )

        // Checked after 4 days (grace period expired)
        val banner = manager.generateBannerAlert(sub, currentTimeMillis = graceEnd + 1000L)
        assertNotNull(banner)
        assertEquals("Assinatura suspensa por inadimplência", banner?.title)
        assertTrue(banner?.isCritical == true)
    }
}
