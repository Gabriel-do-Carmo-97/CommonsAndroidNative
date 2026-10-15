package br.com.wgc.driver_app.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [NavigationLauncher] validando construção de especificações de Intent
 * para Google Maps, Waze e intent genérico do sistema.
 */
class NavigationLauncherTest {

    private lateinit var launcher: NavigationLauncher

    @Before
    fun setup() {
        launcher = NavigationLauncher()
    }

    @Test
    fun `buildNavigationSpec for Google Maps includes google navigation scheme and package`() {
        val spec = launcher.buildNavigationSpec(
            app = ExternalNavigationApp.GOOGLE_MAPS,
            destLatitude = -23.5505,
            destLongitude = -46.6333,
            destinationLabel = "Padaria Central"
        )

        assertEquals(NavigationLauncher.ACTION_VIEW, spec.action)
        assertEquals(NavigationLauncher.PACKAGE_GOOGLE_MAPS, spec.targetPackage)
        assertTrue(spec.uriString.startsWith("google.navigation:q=-23.5505,-46.6333"))
        assertTrue(spec.uriString.contains("Padaria Central"))
    }

    @Test
    fun `buildNavigationSpec for Waze includes correct deep link and package`() {
        val spec = launcher.buildNavigationSpec(
            app = ExternalNavigationApp.WAZE,
            destLatitude = -23.5505,
            destLongitude = -46.6333
        )

        assertEquals(NavigationLauncher.ACTION_VIEW, spec.action)
        assertEquals(NavigationLauncher.PACKAGE_WAZE, spec.targetPackage)
        assertEquals("https://waze.com/ul?ll=-23.5505,-46.6333&navigate=yes", spec.uriString)
    }

    @Test
    fun `buildNavigationSpec for SYSTEM_DEFAULT creates geo intent with null package`() {
        val spec = launcher.buildNavigationSpec(
            app = ExternalNavigationApp.SYSTEM_DEFAULT,
            destLatitude = -23.5505,
            destLongitude = -46.6333
        )

        assertEquals(NavigationLauncher.ACTION_VIEW, spec.action)
        assertNull(spec.targetPackage)
        assertTrue(spec.uriString.startsWith("geo:-23.5505,-46.6333"))
    }
}
