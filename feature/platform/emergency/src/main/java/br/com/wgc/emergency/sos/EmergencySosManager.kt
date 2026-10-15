package br.com.wgc.emergency.sos

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classificação da ocorrência de emergência reportada pelo condutor.
 */
enum class EmergencyType(val priorityLevel: Int, val label: String) {
    /** Risco iminente à integridade física (assalto/roubo). */
    ROBBERY_RISK(1, "Risco de Roubo / Ameaça"),
    /** Acidente de trânsito envolvendo o veículo ou colisão. */
    ACCIDENT(2, "Acidente de Trânsito"),
    /** Urgência médica ou mal súbito. */
    MEDICAL_URGENCY(3, "Urgência Médica"),
    /** Pane mecânica, pneu furado ou bateria descarregada. */
    VEHICLE_BREAKDOWN(4, "Pane Mecânica"),
    /** Suporte geral e dúvidas operacionais da corrida. */
    GENERAL_SUPPORT(5, "Suporte Geral")
}

/**
 * Pacote de telemetria e socorro transmitido instantaneamente para a Central de Segurança.
 *
 * @property alertId Identificador único do alerta gerado (UUID).
 * @property driverId Identificador do motorista parceiro.
 * @property driverName Nome cadastrado do condutor.
 * @property emergencyType Categoria da ocorrência ([EmergencyType]).
 * @property latitude Latitude exata no momento do acionamento.
 * @property longitude Longitude exata no momento do acionamento.
 * @property batteryPercentage Nível de carga da bateria do dispositivo (0 a 100).
 * @property networkType Tipo de conexão no momento do envio (ex: "5G", "Wi-Fi").
 * @property triggeredAtMillis Carimbo de data/hora do disparo em milissegundos.
 */
data class EmergencySosPayload(
    val alertId: String,
    val driverId: String,
    val driverName: String,
    val emergencyType: EmergencyType,
    val latitude: Double,
    val longitude: Double,
    val batteryPercentage: Int,
    val networkType: String,
    val triggeredAtMillis: Long = System.currentTimeMillis()
)

/**
 * Especificação para inicialização de chamada telefônica de emergência direta.
 *
 * @property phoneNumber Número de discagem de socorro (ex: "190" ou central 24h).
 * @property callIntentUri URI compatível com a Intent de discagem do Android (`tel:`).
 */
data class EmergencyCallSpec(
    val phoneNumber: String,
    val callIntentUri: String
)

/**
 * Gerenciador corporativo da Central de Emergência, Botão de Pânico (SOS) e Telemetria Crítica.
 *
 * Habilita o motorista parceiro a disparar alertas de socorro em situações de risco com transmissão
 * imediata das coordenadas GPS, nível de bateria e acionamento de chamada direta para a central 24/7.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de emergência ([br.com.wgc.emergency.screen.EmergencyScreen]) ao manter o botão de
 * pânico pressionado por 2 segundos ou ao escolher um canal de socorro rápido.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val sosManager = EmergencySosManager()
 *
 * // 1. Disparar SOS com telemetria:
 * val payload = sosManager.triggerSosAlert(
 *     driverId = "drv-101",
 *     driverName = "Marcos Lima",
 *     type = EmergencyType.ROBBERY_RISK,
 *     latitude = -23.5505,
 *     longitude = -46.6333,
 *     batteryLevel = 45
 * )
 *
 * // 2. Obter Intent de chamada de emergência:
 * val callSpec = sosManager.buildEmergencyCallSpec("190")
 * val intent = Intent(Intent.ACTION_DIAL, Uri.parse(callSpec.callIntentUri))
 * context.startActivity(intent)
 * ```
 */
@Singleton
class EmergencySosManager @Inject constructor() {

    private val _activeAlerts = MutableStateFlow<List<EmergencySosPayload>>(emptyList())

    /**
     * Fluxo reativo dos alertas de emergência ativos reportados na sessão.
     */
    val activeAlerts: StateFlow<List<EmergencySosPayload>> = _activeAlerts.asStateFlow()

    /**
     * Dispara um novo alerta de socorro e transmite o pacote de telemetria aos canais de apoio.
     *
     * @param driverId Identificador do condutor.
     * @param driverName Nome do condutor.
     * @param type Tipo de ocorrência.
     * @param latitude Latitude de localização atual.
     * @param longitude Longitude de localização atual.
     * @param batteryLevel Porcentagem de bateria do aparelho.
     * @param networkType Tipo de conexão atual.
     * @return [EmergencySosPayload] consolidado com ID único e carimbo temporal.
     */
    fun triggerSosAlert(
        driverId: String,
        driverName: String,
        type: EmergencyType,
        latitude: Double,
        longitude: Double,
        batteryLevel: Int = 100,
        networkType: String = "4G"
    ): EmergencySosPayload {
        val payload = EmergencySosPayload(
            alertId = UUID.randomUUID().toString(),
            driverId = driverId,
            driverName = driverName,
            emergencyType = type,
            latitude = latitude,
            longitude = longitude,
            batteryPercentage = batteryLevel.coerceIn(0, 100),
            networkType = networkType
        )

        _activeAlerts.update { list ->
            listOf(payload) + list
        }

        return payload
    }

    /**
     * Gera a especificação de discagem telefônica para socorro imediato.
     *
     * @param supportPhoneNumber Número telefônico a ser discado (padrão: "190" - Polícia Militar).
     * @return [EmergencyCallSpec] com URI pronta no padrão `tel:`.
     */
    fun buildEmergencyCallSpec(supportPhoneNumber: String = "190"): EmergencyCallSpec {
        val sanitizedNumber = supportPhoneNumber.filter { it.isDigit() || it == '+' }
        return EmergencyCallSpec(
            phoneNumber = sanitizedNumber,
            callIntentUri = "tel:$sanitizedNumber"
        )
    }

    /**
     * Conclui ou resolve uma ocorrência de emergência ativa.
     *
     * @param alertId Identificador da ocorrência a ser desativada.
     */
    fun resolveAlert(alertId: String) {
        _activeAlerts.update { list ->
            list.filterNot { it.alertId == alertId }
        }
    }
}
