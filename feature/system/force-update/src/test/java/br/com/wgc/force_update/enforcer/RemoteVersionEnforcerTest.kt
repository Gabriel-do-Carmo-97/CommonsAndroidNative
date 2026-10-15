package br.com.wgc.force_update.enforcer

import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [RemoteVersionEnforcer].
 */
class RemoteVersionEnforcerTest {

    private lateinit var enforcer: RemoteVersionEnforcer
    private val fakeUri = mockk<Uri>()

    @Before
    fun setup() {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns fakeUri
        enforcer = RemoteVersionEnforcer()
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `evaluate quando manutencao ativa deve retornar MAINTENANCE_MODE e bloquear`() {
        val policy = VersionPolicy(
            currentVersionCode = 20L,
            minRequiredVersionCode = 10L,
            latestAvailableVersionCode = 20L,
            isMaintenanceActive = true,
            maintenanceMessage = "Servidores em atualização programada"
        )

        val result = enforcer.evaluate(policy)

        assertEquals(AppVersionStatus.MAINTENANCE_MODE, result.status)
        assertTrue(result.isBlocked)
        assertEquals("Servidores em atualização programada", result.alertMessage)
    }

    @Test
    fun `evaluate quando versao atual menor que minima deve retornar FORCE_UPDATE_REQUIRED e bloquear`() {
        val policy = VersionPolicy(
            currentVersionCode = 5L,
            minRequiredVersionCode = 10L,
            latestAvailableVersionCode = 15L
        )

        val result = enforcer.evaluate(policy)

        assertEquals(AppVersionStatus.FORCE_UPDATE_REQUIRED, result.status)
        assertTrue(result.isBlocked)
    }

    @Test
    fun `evaluate quando versao atual atende minima mas menor que mais recente deve retornar OPTIONAL_UPDATE`() {
        val policy = VersionPolicy(
            currentVersionCode = 10L,
            minRequiredVersionCode = 10L,
            latestAvailableVersionCode = 15L
        )

        val result = enforcer.evaluate(policy)

        assertEquals(AppVersionStatus.OPTIONAL_UPDATE_AVAILABLE, result.status)
        assertFalse(result.isBlocked)
    }

    @Test
    fun `evaluate quando versao atual e a mais recente deve retornar UP_TO_DATE`() {
        val policy = VersionPolicy(
            currentVersionCode = 15L,
            minRequiredVersionCode = 10L,
            latestAvailableVersionCode = 15L
        )

        val result = enforcer.evaluate(policy)

        assertEquals(AppVersionStatus.UP_TO_DATE, result.status)
        assertFalse(result.isBlocked)
    }
}
