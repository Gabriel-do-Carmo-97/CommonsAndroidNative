package br.com.wgc.media_picker.compression

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes unitários para cálculos de dimensionamento e proporções do pipeline de compressão.
 */
class ImageCompressionPipelineTest {

    @Test
    fun `calculateInSampleSize com imagem menor que maxDimension deve retornar 1`() {
        val sampleSize = ImageDimensionCalculator.calculateInSampleSize(
            originalWidth = 800,
            originalHeight = 600,
            maxDimension = 1920
        )
        assertEquals(1, sampleSize)
    }

    @Test
    fun `calculateInSampleSize com imagem 4K deve calcular subamostragem correta`() {
        val sampleSize = ImageDimensionCalculator.calculateInSampleSize(
            originalWidth = 3840,
            originalHeight = 2160,
            maxDimension = 1080
        )
        assertTrue(sampleSize >= 2)
    }

    @Test
    fun `calculateTargetDimensions para imagem em paisagem deve manter proporcao`() {
        val (width, height) = ImageDimensionCalculator.calculateTargetDimensions(
            originalWidth = 4000,
            originalHeight = 2000,
            maxDimension = 1000
        )
        assertEquals(1000, width)
        assertEquals(500, height)
    }

    @Test
    fun `calculateTargetDimensions para imagem em retrato deve manter proporcao`() {
        val (width, height) = ImageDimensionCalculator.calculateTargetDimensions(
            originalWidth = 1000,
            originalHeight = 2000,
            maxDimension = 1000
        )
        assertEquals(500, width)
        assertEquals(1000, height)
    }

    @Test
    fun `calculateTargetDimensions para imagem que ja cabe no limite deve manter dimensoes originais`() {
        val (width, height) = ImageDimensionCalculator.calculateTargetDimensions(
            originalWidth = 800,
            originalHeight = 600,
            maxDimension = 1200
        )
        assertEquals(800, width)
        assertEquals(600, height)
    }
}
