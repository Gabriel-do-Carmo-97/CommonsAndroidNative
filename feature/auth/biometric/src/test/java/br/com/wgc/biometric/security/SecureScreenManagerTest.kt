package br.com.wgc.biometric.security

import android.view.Window
import android.view.WindowManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [SecureScreenManager] validando aplicação e remoção de FLAG_SECURE.
 */
class SecureScreenManagerTest {

    private lateinit var manager: SecureScreenManager
    private lateinit var window: Window

    @Before
    fun setup() {
        manager = SecureScreenManager()
        window = mockk(relaxed = true)
    }

    @Test
    fun `enableSecureMode sets FLAG_SECURE on window`() {
        manager.enableSecureMode(window)

        verify(exactly = 1) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }

    @Test
    fun `disableSecureMode clears FLAG_SECURE on window`() {
        manager.disableSecureMode(window)

        verify(exactly = 1) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    @Test
    fun `isSecureModeActive checks flag presence correctly`() {
        val params = WindowManager.LayoutParams()
        every { window.attributes } returns params

        params.flags = 0
        assertFalse(manager.isSecureModeActive(window))

        params.flags = WindowManager.LayoutParams.FLAG_SECURE
        assertTrue(manager.isSecureModeActive(window))
    }
}
