package br.com.wgc.commonsandroidnative.ui.accessibility

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes unitários para verificação de cálculos de contraste e conformidade WCAG AAA.
 */
class WgcAccessibilityHelperTest {

    @Test
    fun `calculateContrastRatio between pure black and pure white should return 21 to 1`() {
        val black = Color(0xFF000000)
        val white = Color(0xFFFFFFFF)

        val ratio = WgcAccessibilityContrastCalculator.calculateContrastRatio(black, white)
        assertEquals(21.0, ratio, 0.1)
    }

    @Test
    fun `isWcagAaaCompliant returns true for black text on white background`() {
        val black = Color(0xFF000000)
        val white = Color(0xFFFFFFFF)

        val isCompliant = WgcAccessibilityContrastCalculator.isWcagAaaCompliant(black, white, isLargeText = false)
        assertTrue(isCompliant)
    }

    @Test
    fun `isWcagAaaCompliant returns false for light gray text on white background`() {
        val lightGray = Color(0xFFCCCCCC)
        val white = Color(0xFFFFFFFF)

        val isCompliant = WgcAccessibilityContrastCalculator.isWcagAaaCompliant(lightGray, white, isLargeText = false)
        assertFalse(isCompliant)
    }

    @Test
    fun `calculateLuminance returns 1 for white and 0 for black`() {
        val white = Color(0xFFFFFFFF)
        val black = Color(0xFF000000)

        assertEquals(1.0, WgcAccessibilityContrastCalculator.calculateLuminance(white), 0.01)
        assertEquals(0.0, WgcAccessibilityContrastCalculator.calculateLuminance(black), 0.01)
    }
}
