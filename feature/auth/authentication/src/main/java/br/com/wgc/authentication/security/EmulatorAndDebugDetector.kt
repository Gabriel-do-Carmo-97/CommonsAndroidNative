package br.com.wgc.authentication.security

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Propriedades de hardware e build fornecidas para inspeção de ambiente emulado.
 *
 * @property fingerprint Assinatura de compilação (ex: `Build.FINGERPRINT`).
 * @property model Nome do modelo comercial (ex: `Build.MODEL`).
 * @property manufacturer Fabricante do dispositivo (ex: `Build.MANUFACTURER`).
 * @property hardware Identificador da arquitetura do chip (ex: `Build.HARDWARE`).
 * @property product Código do produto da ROM (ex: `Build.PRODUCT`).
 */
data class DeviceHardwareProfile(
    val fingerprint: String,
    val model: String,
    val manufacturer: String,
    val hardware: String,
    val product: String
)

/**
 * Diagnóstico completo do ambiente de execução e acoplamento de ferramentas de depuração.
 *
 * @property isEmulator Sinaliza se o aplicativo está rodando em um emulador Android (AVD, Genymotion, Bluestacks).
 * @property isDebuggerConnected Indica se existe um depurador ativo acoplado à JVM/ART (via JDWP/GDB).
 * @property detectedIndicators Lista de marcadores textuais de emulação encontrados.
 * @property isSafeForRelease Indica se o ambiente é seguro para processamento de transações em builds de produção.
 */
data class EnvironmentInspectionResult(
    val isEmulator: Boolean,
    val isDebuggerConnected: Boolean,
    val detectedIndicators: List<String>,
    val isSafeForRelease: Boolean
)

/**
 * Detector corporativo de Emuladores e Depuradores Ativos (Anti-Debugging & Anti-Tampering).
 *
 * Identifica a execução do aplicativo em ambientes virtuais ou sob depuração de código em tempo real,
 * prevenindo ataques de engenharia reversa, análise dinâmica com Frida/Xposed e manipulação de fluxos.
 *
 * ### 📌 Quando Usar:
 * Acione nas rotinas de verificação de integridade no início da sessão do usuário (:feature:auth:authentication)
 * e bloqueie o carregamento de tokens sensíveis caso depuradores estejam conectados em release.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val detector = EmulatorAndDebugDetector()
 * val result = detector.inspectEnvironment(isReleaseBuild = true)
 *
 * if (!result.isSafeForRelease) {
 *     encerrarAplicativoPorSeguranca("Execução proibida sob depuração ou ambiente emulado.")
 * }
 * ```
 */
@Singleton
class EmulatorAndDebugDetector @Inject constructor() {

    private val emulatorKeywords = listOf(
        "generic",
        "unknown",
        "google_sdk",
        "emulator",
        "android sdk built for",
        "goldfish",
        "ranchu",
        "vbox86",
        "bluestacks",
        "nox"
    )

    /**
     * Inspeciona o perfil de hardware e o estado do depurador no processo do app.
     *
     * @param isReleaseBuild Sinalizador se a variante do build atual é de Release (`!BuildConfig.DEBUG`).
     * @param profile Perfil de hardware injetado (padrão lê propriedades reais do sistema operacional).
     * @param isDebuggerAttached Sinalizador se há debugger ativo (padrão: `android.os.Debug.isDebuggerConnected()`).
     * @return [EnvironmentInspectionResult] consolidado.
     */
    fun inspectEnvironment(
        isReleaseBuild: Boolean,
        profile: DeviceHardwareProfile = DeviceHardwareProfile(
            fingerprint = android.os.Build.FINGERPRINT ?: "",
            model = android.os.Build.MODEL ?: "",
            manufacturer = android.os.Build.MANUFACTURER ?: "",
            hardware = android.os.Build.HARDWARE ?: "",
            product = android.os.Build.PRODUCT ?: ""
        ),
        isDebuggerAttached: Boolean = try {
            android.os.Debug.isDebuggerConnected() || android.os.Debug.waitingForDebugger()
        } catch (_: Exception) {
            false
        }
    ): EnvironmentInspectionResult {
        val indicators = mutableListOf<String>()

        val combinedProps = listOf(
            profile.fingerprint,
            profile.model,
            profile.manufacturer,
            profile.hardware,
            profile.product
        ).map { it.lowercase() }

        for (keyword in emulatorKeywords) {
            if (combinedProps.any { it.contains(keyword) }) {
                indicators.add("Indicador de emulador detectado: '$keyword'")
            }
        }

        val isEmulator = indicators.isNotEmpty()
        // Em release, não é seguro executar se debugger estiver acoplado OU se for emulador
        val isSafeForRelease = if (isReleaseBuild) {
            !isDebuggerAttached && !isEmulator
        } else {
            // Em desenvolvimento local/debug, emuladores e debugger são permitidos
            true
        }

        return EnvironmentInspectionResult(
            isEmulator = isEmulator,
            isDebuggerConnected = isDebuggerAttached,
            detectedIndicators = indicators,
            isSafeForRelease = isSafeForRelease
        )
    }
}
