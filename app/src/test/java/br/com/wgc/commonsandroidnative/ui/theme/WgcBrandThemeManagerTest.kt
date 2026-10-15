package br.com.wgc.commonsandroidnative.ui.theme

import br.com.wgc.design_system.templates.brand.WgcBrand
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test

/**
 * Testes unitários para o gerenciador corporativo de marcas e temas White-Label [WgcBrandThemeManager].
 */
class WgcBrandThemeManagerTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun initAndroidMocks() {
            try {
                mockkStatic(android.graphics.Color::class)
                every { android.graphics.Color.parseColor(any()) } answers { 0 }
            } catch (_: Throwable) {
                // Ignore se já configurado
            }
        }
    }

    @Test
    fun `setBrand updates current brand state and getBrand returns updated value`() {
        WgcBrandThemeManager.setBrand(WgcBrand.FintechNeobank)
        assertEquals(WgcBrand.FintechNeobank, WgcBrandThemeManager.getBrand())
        assertEquals(WgcBrand.FintechNeobank, WgcBrandThemeManager.currentBrand.value)

        WgcBrandThemeManager.setBrand(WgcBrand.Telemedicine)
        assertEquals(WgcBrand.Telemedicine, WgcBrandThemeManager.getBrand())

        WgcBrandThemeManager.setBrand(WgcBrand.Retail)
        assertEquals(WgcBrand.Retail, WgcBrandThemeManager.getBrand())
    }
}
