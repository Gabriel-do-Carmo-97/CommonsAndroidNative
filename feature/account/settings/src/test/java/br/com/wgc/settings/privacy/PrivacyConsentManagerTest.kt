package br.com.wgc.settings.privacy

import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [PrivacyConsentManager] validando persistência de consentimento,
 * revalidação por versão de política e revogação de categorias.
 */
class PrivacyConsentManagerTest {

    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var manager: PrivacyConsentManager

    @Before
    fun setup() {
        storage = InMemoryKeyValueStorage()
        manager = PrivacyConsentManager(storage)
    }

    @Test
    fun `isConsentRevalidationRequired returns true initially and false after consent`() {
        assertTrue(manager.isConsentRevalidationRequired(currentPolicyVersion = "1.0"))
        assertFalse(manager.hasConsented())

        val prefs = PrivacyConsentPreferences(
            analyticsAccepted = true,
            marketingAccepted = false,
            personalizationAccepted = true,
            policyVersion = "1.0"
        )
        manager.saveConsent(prefs)

        assertTrue(manager.hasConsented())
        assertFalse(manager.isConsentRevalidationRequired(currentPolicyVersion = "1.0"))

        // When policy version changes to 2.0, revalidation is required
        assertTrue(manager.isConsentRevalidationRequired(currentPolicyVersion = "2.0"))
    }

    @Test
    fun `getConsent restores exact saved choices`() {
        val t0 = 1700000000000L
        val prefs = PrivacyConsentPreferences(
            analyticsAccepted = true,
            marketingAccepted = true,
            personalizationAccepted = false,
            consentedAtMillis = t0,
            policyVersion = "1.5"
        )
        manager.saveConsent(prefs)

        val retrieved = manager.getConsent()
        assertTrue(retrieved.essentialAccepted)
        assertTrue(retrieved.analyticsAccepted)
        assertTrue(retrieved.marketingAccepted)
        assertFalse(retrieved.personalizationAccepted)
        assertEquals(t0, retrieved.consentedAtMillis)
        assertEquals("1.5", retrieved.policyVersion)
    }

    @Test
    fun `revokeConsentCategory turns off specific consent while keeping others`() {
        val prefs = PrivacyConsentPreferences(
            analyticsAccepted = true,
            marketingAccepted = true,
            personalizationAccepted = true
        )
        manager.saveConsent(prefs)

        manager.revokeConsentCategory(ConsentCategory.MARKETING)

        val updated = manager.getConsent()
        assertTrue(updated.analyticsAccepted)
        assertFalse(updated.marketingAccepted)
        assertTrue(updated.personalizationAccepted)
    }
}
