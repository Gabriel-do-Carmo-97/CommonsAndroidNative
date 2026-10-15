package br.com.wgc.commonsandroidnative.ui.accessibility

import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes unitários para o utilitário de tipografia dinâmica [WgcDynamicTypeHelper].
 */
class WgcDynamicTypeHelperTest {

    @Test
    fun `isHighFontScale returns false for default scale 1_0`() {
        assertFalse(WgcDynamicTypeHelper.isHighFontScale(1.0f))
    }

    @Test
    fun `isHighFontScale returns true for scale 1_3 and 2_0`() {
        assertTrue(WgcDynamicTypeHelper.isHighFontScale(1.30f))
        assertTrue(WgcDynamicTypeHelper.isHighFontScale(2.00f))
    }

    @Test
    fun `clampScaledTextSize respects maxScale cap of 2_0`() {
        val baseSize = 16f
        val clamped = WgcDynamicTypeHelper.clampScaledTextSize(baseSize, currentFontScale = 2.5f, maxScale = 2.0f)
        assertEquals(32.sp, clamped)
    }

    @Test
    fun `clampScaledTextSize calculates exact scale below maxScale`() {
        val baseSize = 14f
        val clamped = WgcDynamicTypeHelper.clampScaledTextSize(baseSize, currentFontScale = 1.5f, maxScale = 2.0f)
        assertEquals(21.sp, clamped)
    }
}
