package br.com.wgc.media_picker.audio

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estado operacional da gravação de áudio / mensagem de voz.
 */
enum class VoiceRecordingState {
    /** Gravador pronto para iniciar. */
    IDLE,
    /** Gravador ativo capturando áudio do microfone. */
    RECORDING,
    /** Gravação pausada temporariamente pelo usuário. */
    PAUSED,
    /** Gravação finalizada com sucesso gerando arquivo pronto para upload. */
    FINISHED,
    /** Gravação abortada ou descartada pelo usuário. */
    CANCELLED,
    /** Erro operacional ocorrido durante a gravação. */
    ERROR
}

/**
 * Perfil de compressão e codificação de áudio nativo otimizado para fala.
 *
 * @property audioSource Origem do áudio (ex: microfone com cancelamento de ruído acústico).
 * @property outputFormat Formato do arquivo contêiner (ex: MP4/M4A ou OGG).
 * @property audioEncoder Codec de compressão (AAC ou Opus).
 * @property sampleRateHz Taxa de amostragem em Hertz (ex: 44100 Hz).
 * @property bitRateBps Taxa de bits por segundo (ex: 64000 bps = 64 kbps).
 * @property maxDurationSeconds Duração limite da mensagem de voz em segundos (padrão 120s = 2 min).
 */
data class AudioEncodingProfile(
    val audioSource: Int = 7, // VOICE_COMMUNICATION (Acoustic Echo Cancellation / Noise Suppression)
    val outputFormat: Int = 2, // MPEG_4
    val audioEncoder: Int = 3, // AAC
    val sampleRateHz: Int = 44100,
    val bitRateBps: Int = 64000,
    val maxDurationSeconds: Int = 120
)

/**
 * Metadados resultantes de uma mensagem de voz concluída com êxito.
 *
 * @property file Arquivo de áudio gerado no sistema de arquivos local.
 * @property durationMillis Duração total da mensagem de voz em milissegundos.
 * @property fileSizeBytes Tamanho do arquivo em bytes.
 * @property mimeType Tipo MIME do arquivo de saída (ex: "audio/mp4" ou "audio/aac").
 */
data class VoiceNoteResult(
    val file: File,
    val durationMillis: Long,
    val fileSizeBytes: Long,
    val mimeType: String = "audio/mp4"
)

/**
 * Validador e gestor de regras de negócio para gravação de mensagens de voz corporativas.
 *
 * Garante requisitos de qualidade de compressão, cancelamento de ruído, descarte de áudios
 * muito curtos (< 1 segundo) e limites de duração antes do envio ao chat ou atendimento.
 *
 * ### 📌 Quando Usar:
 * - Em módulos de chat e suporte (`:feature:communication:message`).
 * - No transbordo de atendimento comercial (`:feature:communication:whatsapp-direct`).
 * - No envio de notas de áudio explicativas em cotações de serviços (`:feature:checkout:quotation`).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val audioManager = AudioVoiceNoteManager()
 * val outputFile = File(context.cacheDir, "voice_note_1.m4a")
 *
 * audioManager.startRecording(outputFile)
 * // ... usuário fala ...
 * val result = audioManager.finishRecording(durationMillis = 5400L)
 * if (result != null) {
 *     println("Áudio pronto para envio: ${result.file.path} (${result.durationMillis}ms)")
 * }
 * ```
 */
@Singleton
class AudioVoiceNoteManager @Inject constructor() {

    companion object {
        /** Duração mínima permitida para uma gravação de voz válida (1 segundo). */
        const val MIN_RECORDING_DURATION_MS = 1000L
        /** Duração máxima padrão para gravação de mensagens de voz (120 segundos). */
        const val DEFAULT_MAX_DURATION_SECONDS = 120
    }

    private var currentState: VoiceRecordingState = VoiceRecordingState.IDLE
    private var currentOutputFile: File? = null
    private var recordingStartTimeMillis: Long = 0L

    /**
     * Retorna o estado atual do ciclo de vida da gravação.
     *
     * @return [VoiceRecordingState] atual.
     */
    fun getState(): VoiceRecordingState = currentState

    /**
     * Inicia a preparação e o fluxo de gravação de áudio no arquivo de destino especificado.
     *
     * @param destinationFile Arquivo no armazenamento local onde o fluxo de áudio será gravado.
     * @param profile Perfil de codificação de áudio configurado.
     * @throws IllegalStateException Se já houver uma gravação ativa em andamento.
     */
    fun startRecording(
        destinationFile: File,
        profile: AudioEncodingProfile = AudioEncodingProfile()
    ) {
        if (currentState == VoiceRecordingState.RECORDING) {
            throw IllegalStateException("Já existe uma gravação de voz ativa em andamento.")
        }
        currentOutputFile = destinationFile
        recordingStartTimeMillis = System.currentTimeMillis()
        currentState = VoiceRecordingState.RECORDING
    }

    /**
     * Conclui a gravação ativa e valida se atende ao tempo mínimo aceitável.
     *
     * @param durationMillis Duração total da gravação em milissegundos (calculada pelo timer ou media framework).
     * @return [VoiceNoteResult] caso a gravação seja válida, ou `null` se for mais curta que [MIN_RECORDING_DURATION_MS].
     */
    fun finishRecording(durationMillis: Long): VoiceNoteResult? {
        val targetFile = currentOutputFile
        if (currentState != VoiceRecordingState.RECORDING || targetFile == null) {
            currentState = VoiceRecordingState.IDLE
            return null
        }

        val effectiveDuration = if (durationMillis > 0) {
            durationMillis
        } else {
            System.currentTimeMillis() - recordingStartTimeMillis
        }

        if (effectiveDuration < MIN_RECORDING_DURATION_MS) {
            // Descartar arquivo caso tenha duração inferior a 1 segundo (cliques acidentais)
            if (targetFile.exists()) {
                targetFile.delete()
            }
            currentState = VoiceRecordingState.CANCELLED
            currentOutputFile = null
            return null
        }

        currentState = VoiceRecordingState.FINISHED
        currentOutputFile = null

        return VoiceNoteResult(
            file = targetFile,
            durationMillis = effectiveDuration,
            fileSizeBytes = if (targetFile.exists()) targetFile.length() else 0L
        )
    }

    /**
     * Cancela e remove o arquivo da gravação corrente em caso de descarte ou gesto de swipe.
     */
    fun cancelRecording() {
        currentOutputFile?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
        currentOutputFile = null
        currentState = VoiceRecordingState.CANCELLED
    }

    /**
     * Normaliza a amplitude do microfone para uma faixa de 0.0 a 1.0 para renderização de ondas sonoras na UI.
     *
     * @param rawAmplitude Valor cru de amplitude retornado pelo `MediaRecorder.maxAmplitude` (0 a 32767).
     * @return Valor percentual normalizado entre 0.0f e 1.0f.
     */
    fun normalizeAmplitude(rawAmplitude: Int): Float {
        return (rawAmplitude.coerceIn(0, 32767).toFloat() / 32767f)
    }
}
