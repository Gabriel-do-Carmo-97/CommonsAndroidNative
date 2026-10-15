package br.com.wgc.authentication.security

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Razões identificadas que comprometem a integridade operacional do sistema operacional Android.
 */
enum class RootDetectionReason(val description: String) {
    /** Binário `su` encontrado nos diretórios de execução do sistema. */
    SU_BINARY_FOUND("Binário de superusuário (su) detectado no sistema de arquivos."),
    /** Build do Android compilada com chaves de teste não confiáveis (test-keys). */
    TEST_KEYS_BUILD("Sistema operacional compilado com chaves não oficiais (test-keys)."),
    /** Aplicativo ou binário associado ao Magisk / KernelSU identificado. */
    MAGISK_DETECTED("Ferramentas de modificação de kernel/Magisk identificadas."),
    /** Aplicativo Superuser.apk ou gerenciador de root instalado. */
    SUPERUSER_APP_PRESENT("Aplicativo de controle de root detectado."),
    /** Binário BusyBox identificado no path do sistema. */
    BUSYBOX_BINARY_FOUND("Binário BusyBox detectado.")
}

/**
 * Diagnóstico de integridade e segurança do ambiente de execução do dispositivo.
 *
 * @property isCompromised Indica se o dispositivo apresenta vulnerabilidades de root ou jailbreak.
 * @property detectedThreats Lista de ameaças e evidências específicas encontradas.
 * @property shouldBlockFinancialOperations Indica se transações de checkout e pagamentos devem ser bloqueadas.
 */
data class DeviceSecurityIntegrity(
    val isCompromised: Boolean,
    val detectedThreats: List<RootDetectionReason>,
    val shouldBlockFinancialOperations: Boolean = isCompromised
)

/**
 * Detector corporativo de Dispositivos Comprometidos (Root, Superuser & Jailbreak).
 *
 * Inspeciona múltiplos vetores de vulnerabilidade do sistema de arquivos Android para identificar
 * privilégios de superusuário que possibilitem injeção de código, interceptação de memória ou
 * bypass de certificados SSL, garantindo conformidade com padrões de segurança bancária.
 *
 * ### 📌 Quando Usar:
 * Execute na inicialização do aplicativo ou antes de concluir transações no fluxo de checkout
 * (:feature:checkout:payment e :feature:auth:authentication) para suspender operações em aparelhos modificados.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val rootDetector = RootSecurityDetector()
 * val integrity = rootDetector.checkDeviceIntegrity()
 *
 * if (integrity.shouldBlockFinancialOperations) {
 *     exibirBloqueioDeSeguranca("Operação financeira não permitida em dispositivos com root.")
 * }
 * ```
 */
@Singleton
class RootSecurityDetector @Inject constructor() {

    companion object {
        val KNOWN_SU_PATHS = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su",
            "/system/app/Superuser.apk"
        )

        val KNOWN_MAGISK_PATHS = listOf(
            "/sbin/.magisk",
            "/data/adb/magisk",
            "/data/adb/ksu"
        )

        val KNOWN_BUSYBOX_PATHS = listOf(
            "/system/bin/busybox",
            "/system/xbin/busybox"
        )
    }

    /**
     * Realiza a varredura de integridade de segurança no sistema operacional.
     *
     * @param fileChecker Função opcional para validação de existência de arquivos (facilita testes unitários).
     * @param buildTags Provedor dos metadados de tags da build do SO (ex: `Build.TAGS`).
     * @return [DeviceSecurityIntegrity] consolidado com lista de ameaças detectadas.
     */
    fun checkDeviceIntegrity(
        fileChecker: (String) -> Boolean = { path -> File(path).exists() },
        buildTags: String? = null
    ): DeviceSecurityIntegrity {
        val threats = mutableListOf<RootDetectionReason>()

        // 1. Verificação de binários 'su'
        if (KNOWN_SU_PATHS.any { fileChecker(it) }) {
            threats.add(RootDetectionReason.SU_BINARY_FOUND)
        }

        // 2. Verificação de artefatos do Magisk / KernelSU
        if (KNOWN_MAGISK_PATHS.any { fileChecker(it) }) {
            threats.add(RootDetectionReason.MAGISK_DETECTED)
        }

        // 3. Verificação de binários BusyBox
        if (KNOWN_BUSYBOX_PATHS.any { fileChecker(it) }) {
            threats.add(RootDetectionReason.BUSYBOX_BINARY_FOUND)
        }

        // 4. Verificação de tags de compilação não confiáveis
        if (buildTags != null && buildTags.contains("test-keys")) {
            threats.add(RootDetectionReason.TEST_KEYS_BUILD)
        }

        val isCompromised = threats.isNotEmpty()
        return DeviceSecurityIntegrity(
            isCompromised = isCompromised,
            detectedThreats = threats,
            shouldBlockFinancialOperations = isCompromised
        )
    }
}
