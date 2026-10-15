package br.com.wgc.commonsandroidnative.ui.theme

import br.com.wgc.design_system.core.WgcBrandTheme
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testes unitários para o gerenciador corporativo de marcas e temas White-Label [WgcBrandThemeManager].
 */
class WgcBrandThemeManagerTest {

    @Test
    fun `setBrand updates current brand state and getBrand returns updated value`() {
        val financeTheme = WgcBrandTheme.finance()
        WgcBrandThemeManager.setBrand(financeTheme)
        assertEquals(financeTheme, WgcBrandThemeManager.getBrand())
        assertEquals(financeTheme, WgcBrandThemeManager.currentBrand.value)

        val ecommerceTheme = WgcBrandTheme.ecommerce()
        WgcBrandThemeManager.setBrand(ecommerceTheme)
        assertEquals(ecommerceTheme, WgcBrandThemeManager.getBrand())

        val fashionTheme = WgcBrandTheme.fashion()
        WgcBrandThemeManager.setBrand(fashionTheme)
        assertEquals(fashionTheme, WgcBrandThemeManager.getBrand())
    }
}
