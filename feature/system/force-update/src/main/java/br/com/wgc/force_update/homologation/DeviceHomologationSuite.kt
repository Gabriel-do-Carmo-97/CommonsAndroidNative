package br.com.wgc.force_update.homologation

import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fabricantes de dispositivos móveis com comportamento customizado de otimização de bateria.
 */
enum class DeviceManufacturer(val rawName: String) {
    SAMSUNG("samsung"),
    XIAOMI("xiaomi"),
    MOTOROLA("motorola"),
    GOOGLE("google"),
    OTHER("other");

    companion object {
        fun fromBuildManufacturer(name: String?): DeviceManufacturer {
            val lower = name?.lowercase() ?: return OTHER
            return entries.firstOrNull { lower.contains(it.rawName) } ?: OTHER
        }
    }
}

/**
 * Parecer detalhado da homologação de compatibilidade do dispositivo.
 *
 * @property isCompatible Indica se o aparelho atende aos requisitos operacionais mínimos.
 * @property apiLevel Nível da API do sistema operacional Android em execução.
 * @property manufacturer Fabricante identificado do hardware.
 * @property recommendations Recomendações de configuração de permissões e bateria para o usuário.
 */
data class HomologationResult(
    val isCompatible: Boolean,
    val apiLevel: Int,
    val manufacturer: DeviceManufacturer,
    val recommendations: List<String>
)

/**
 * Suíte corporativa de homologação de compatibilidade em dispositivos de múltiplos fabricantes (OEMs).
 *
 * Avalia compatibilidade de API (Android 8.0 / API 26 até Android 15 / API 35) e emite
 * recomendações específicas para contornar agressividade de otimização de bateria (Doze Mode)
 * típica de aparelhos Samsung e Xiaomi, garantindo a pontualidade do rastreamento de entregas e sincronização offline.
 *
 * ### 📌 Quando Usar:
 * - Na inicialização do app para detecção precoce de incompatibilidades de hardware.
 * - Ao solicitar permissões de localização contínua em segundo plano (`:feature:delivery:maps`).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val homologation = DeviceHomologationSuite()
 * val result = homologation.evaluateDevice(apiLevel = Build.VERSION.SDK_INT, manufacturerName = Build.MANUFACTURER)
 * if (!result.isCompatible) {
 *     // exibir tela informativa de requisitos
 * }
 * ```
 */
@Singleton
class DeviceHomologationSuite @Inject constructor() {

    companion object {
        /** API mínima homologada pelo ecossistema WGC (Android 8.0 Oreo). */
        const val MIN_SUPPORTED_API = 26
        /** API máxima testada e homologada (Android 15 Vanilla Ice Cream). */
        const val MAX_SUPPORTED_API = 35
    }

    /**
     * Avalia o dispositivo contra a matriz de homologação corporativa.
     *
     * @param apiLevel Versão numérica da API Android (`Build.VERSION.SDK_INT`).
     * @param manufacturerName Nome do fabricante retornado por `Build.MANUFACTURER`.
     * @return [HomologationResult] com status e orientações de compatibilidade.
     */
    fun evaluateDevice(
        apiLevel: Int = Build.VERSION.SDK_INT,
        manufacturerName: String = Build.MANUFACTURER
    ): HomologationResult {
        val manufacturer = DeviceManufacturer.fromBuildManufacturer(manufacturerName)
        val recommendations = mutableListOf<String>()

        val isCompatible = apiLevel >= MIN_SUPPORTED_API

        if (!isCompatible) {
            recommendations.add("Versão do sistema Android não suportada. É necessário Android 8.0 (API 26) ou superior.")
        }

        when (manufacturer) {
            DeviceManufacturer.XIAOMI -> {
                recommendations.add("Desative o 'Economizador de bateria MIUI' nas configurações do app para rastreamento pontual.")
            }
            DeviceManufacturer.SAMSUNG -> {
                recommendations.add("Adicione o aplicativo à lista de 'Aplicativos nunca suspensos' no menu de Cuidados do Aparelho.")
            }
            DeviceManufacturer.MOTOROLA -> {
                recommendations.add("Garanta que a otimização de bateria esteja em 'Não otimizado' para notificações em tempo real.")
            }
            DeviceManufacturer.GOOGLE, DeviceManufacturer.OTHER -> {
                // Sem instruções agressivas necessárias
            }
        }

        return HomologationResult(
            isCompatible = isCompatible,
            apiLevel = apiLevel,
            manufacturer = manufacturer,
            recommendations = recommendations
        )
    }
}
