package br.com.wgc.media_picker.compression

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configuração das metas de compressão e redimensionamento de imagem.
 *
 * @property maxDimensionPx Dimensão máxima (largura ou altura) permitida em pixels.
 * @property targetQuality Qualidade da compressão com perdas (0 a 100).
 * @property compressFormat Formato de destino para compressão (padrão WEBP).
 */
data class CompressionConfig(
    val maxDimensionPx: Int = 1920,
    val targetQuality: Int = 80,
    val compressFormat: Bitmap.CompressFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Bitmap.CompressFormat.WEBP_LOSSY
    } else {
        @Suppress("DEPRECATION")
        Bitmap.CompressFormat.WEBP
    }
)

/**
 * Resultado da operação de compressão com telemetria de redução de peso.
 *
 * @property outputBytes Bytes do arquivo de imagem comprimido.
 * @property originalSizeBytes Tamanho original dos bytes em disco ou memória.
 * @property compressedSizeBytes Tamanho final dos bytes após compressão.
 * @property width Largura final da imagem em pixels.
 * @property height Altura final da imagem em pixels.
 * @property reductionPercentage Percentual de economia de banda alcançado (0.0 a 100.0).
 */
data class CompressionResult(
    val outputBytes: ByteArray,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val width: Int,
    val height: Int,
    val reductionPercentage: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CompressionResult
        return outputBytes.contentEquals(other.outputBytes) &&
                originalSizeBytes == other.originalSizeBytes &&
                compressedSizeBytes == other.compressedSizeBytes &&
                width == other.width &&
                height == other.height
    }

    override fun hashCode(): Int {
        var result = outputBytes.contentHashCode()
        result = 31 * result + originalSizeBytes.hashCode()
        result = 31 * result + compressedSizeBytes.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}

/**
 * Calculador de subamostragem e proporções para redimensionamento de imagens sem estouro de memória (OOM).
 */
object ImageDimensionCalculator {
    /**
     * Calcula o fator de subamostragem `inSampleSize` proporcional à potência de 2.
     *
     * @param originalWidth Largura original da imagem.
     * @param originalHeight Altura original da imagem.
     * @param maxDimension Dimensão máxima desejada.
     * @return Fator inteiro de escala para decodificação eficiente.
     */
    fun calculateInSampleSize(originalWidth: Int, originalHeight: Int, maxDimension: Int): Int {
        var inSampleSize = 1
        if (originalHeight > maxDimension || originalWidth > maxDimension) {
            val halfHeight = originalHeight / 2
            val halfWidth = originalWidth / 2
            while ((halfHeight / inSampleSize) >= maxDimension && (halfWidth / inSampleSize) >= maxDimension) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * Calcula as novas dimensões de largura e altura mantendo estritamente a proporção de tela original.
     *
     * @param originalWidth Largura original.
     * @param originalHeight Altura original.
     * @param maxDimension Dimensão limite.
     * @return Par contendo `(novaLargura, novaAltura)`.
     */
    fun calculateTargetDimensions(originalWidth: Int, originalHeight: Int, maxDimension: Int): Pair<Int, Int> {
        if (originalWidth <= maxDimension && originalHeight <= maxDimension) {
            return Pair(originalWidth, originalHeight)
        }
        val ratio = originalWidth.toFloat() / originalHeight.toFloat()
        return if (originalWidth > originalHeight) {
            val targetW = maxDimension
            val targetH = (maxDimension / ratio).toInt().coerceAtLeast(1)
            Pair(targetW, targetH)
        } else {
            val targetH = maxDimension
            val targetW = (maxDimension * ratio).toInt().coerceAtLeast(1)
            Pair(targetW, targetH)
        }
    }
}

/**
 * Pipeline corporativo para compressão inteligente de imagens antes de upload para o OmniBackend / Cloud Storage.
 *
 * Realiza subamostragem em duas etapas (decodificação de limites com `inJustDecodeBounds` seguida de redimensionamento
 * suave com compressão WebP em 80% de qualidade), reduzindo o payload de fotos de câmeras de ~8MB para ~250KB sem
 * perda perceptível de nitidez em dispositivos móveis.
 *
 * ### 📌 Quando Usar:
 * - Em fluxos de seleção de foto de perfil (`:feature:account:profile`).
 * - No upload de catálogos e anúncios (`:feature:storefront:catalog`).
 * - No envio de fotos de cotação ou anexos no chat (`:feature:communication:message`).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val pipeline = ImageCompressionPipeline()
 * val result = pipeline.compress(
 *     inputStream = context.contentResolver.openInputStream(uri)!!,
 *     originalSizeBytes = file.length(),
 *     config = CompressionConfig(maxDimensionPx = 1440, targetQuality = 80)
 * )
 *
 * println("Redução: ${result.reductionPercentage}% - Tamanho final: ${result.compressedSizeBytes} bytes")
 * ```
 */
@Singleton
class ImageCompressionPipeline @Inject constructor() {

    /**
     * Executa a pipeline completa de leitura, subamostragem, escalonamento e conversão para WebP.
     *
     * @param inputStream Fluxo de bytes da imagem original.
     * @param originalSizeBytes Tamanho total dos bytes do arquivo original.
     * @param config Parâmetros de resolução máxima e fator de qualidade desejados.
     * @return [CompressionResult] contendo os bytes comprimidos e métricas de desempenho.
     */
    fun compress(
        inputStream: InputStream,
        originalSizeBytes: Long,
        config: CompressionConfig = CompressionConfig()
    ): CompressionResult {
        val rawBytes = inputStream.readBytes()
        val originalSize = if (originalSizeBytes > 0) originalSizeBytes else rawBytes.size.toLong()

        // 1. Decodificar apenas dimensões
        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size, boundsOptions)

        val originalWidth = boundsOptions.outWidth
        val originalHeight = boundsOptions.outHeight

        // 2. Subamostragem no decode
        val sampleSize = ImageDimensionCalculator.calculateInSampleSize(
            originalWidth, originalHeight, config.maxDimensionPx
        )

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val sampledBitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size, decodeOptions)
            ?: throw IllegalArgumentException("Falha ao decodificar imagem fornecida.")

        // 3. Redimensionamento preciso
        val (targetW, targetH) = ImageDimensionCalculator.calculateTargetDimensions(
            sampledBitmap.width, sampledBitmap.height, config.maxDimensionPx
        )

        val finalBitmap = if (sampledBitmap.width != targetW || sampledBitmap.height != targetH) {
            val scaled = Bitmap.createScaledBitmap(sampledBitmap, targetW, targetH, true)
            if (scaled != sampledBitmap) {
                sampledBitmap.recycle()
            }
            scaled
        } else {
            sampledBitmap
        }

        // 4. Compressão para WebP
        val outputStream = ByteArrayOutputStream()
        finalBitmap.compress(config.compressFormat, config.targetQuality, outputStream)
        val compressedBytes = outputStream.toByteArray()
        val finalSize = compressedBytes.size.toLong()

        finalBitmap.recycle()

        val reduction = if (originalSize > 0) {
            ((originalSize - finalSize).toFloat() / originalSize.toFloat() * 100f).coerceIn(0f, 100f)
        } else {
            0f
        }

        return CompressionResult(
            outputBytes = compressedBytes,
            originalSizeBytes = originalSize,
            compressedSizeBytes = finalSize,
            width = targetW,
            height = targetH,
            reductionPercentage = reduction
        )
    }
}
