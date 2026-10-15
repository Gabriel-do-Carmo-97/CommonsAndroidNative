package br.com.wgc.scheduling.lock

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 * Status do bloqueio temporário de vaga na agenda do prestador.
 */
enum class SlotReservationStatus {
    /** Vaga bloqueada em hold temporário durante o fluxo de checkout (10 minutos). */
    RESERVED_ACTIVE,
    /** Agendamento pago e confirmado definitivamente. */
    CONFIRMED,
    /** Tempo do bloqueio temporário expirado; vaga devolvida ao catálogo geral. */
    EXPIRED,
    /** Vaga liberada voluntariamente pelo usuário ao cancelar a seleção. */
    RELEASED
}

/**
 * Registro de trava de concorrência para uma faixa de horário específica.
 *
 * @property reservationId Identificador único universal do bloqueio temporário (UUID).
 * @property professionalId Identificador do prestador de serviço selecionado.
 * @property serviceId Identificador do serviço/procedimento a ser executado.
 * @property slotIsoDateTime Data e hora da vaga em formato ISO-8601 (ex: "2026-10-20T14:30:00").
 * @property reservedByUserId Identificador do usuário que detém o lock temporário.
 * @property expiresAtMillis Carimbo de expiração do lock em milissegundos.
 * @property status Estado atual do bloqueio ([SlotReservationStatus]).
 */
data class TimeSlotReservation(
    val reservationId: String,
    val professionalId: String,
    val serviceId: String,
    val slotIsoDateTime: String,
    val reservedByUserId: String,
    val expiresAtMillis: Long,
    val status: SlotReservationStatus = SlotReservationStatus.RESERVED_ACTIVE
)

/**
 * Resultado da tentativa de aquisição de trava temporária de horário.
 */
sealed interface ReservationOutcome {
    /**
     * Trava adquirida com sucesso; vaga reservada exclusivamente para o usuário.
     *
     * @property reservation Registro com os detalhes do lock.
     * @property holdMinutesRemaining Minutos restantes de exclusividade.
     */
    data class Acquired(val reservation: TimeSlotReservation, val holdMinutesRemaining: Int) : ReservationOutcome

    /**
     * Conflito de agenda: horário já selecionado por outro cliente ou confirmado.
     *
     * @property reason Motivo detalhado do conflito.
     */
    data class Conflict(val reason: String) : ReservationOutcome
}

/**
 * Gerenciador corporativo de Agendamento Inteligente com Trava de Concorrência (Hold de 10 minutos).
 *
 * Previne colisões e duplo agendamento simultâneo (Double Booking) através de trava temporária com TTL,
 * assegurando que o horário selecionado permaneça bloqueado exclusivamente para o cliente durante a
 * etapa de checkout e pagamento.
 *
 * ### 📌 Quando Usar:
 * Acione no [br.com.wgc.scheduling.screen.SchedulingViewModel] assim que o usuário clicar em um horário
 * no calendário de agendamento antes de prosseguir para a tela de pagamento.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val lockManager = SchedulingConcurrencyLockManager()
 * val outcome = lockManager.acquireSlotLock(
 *     professionalId = "prof_44",
 *     serviceId = "srv_corte",
 *     slotIsoDateTime = "2026-10-25T15:00:00",
 *     userId = "usr_10"
 * )
 *
 * when (outcome) {
 *     is ReservationOutcome.Acquired -> { navegarParaCheckout(outcome.reservation.reservationId) }
 *     is ReservationOutcome.Conflict -> { alertarHorarioIndisponivel(outcome.reason) }
 * }
 * ```
 */
@Singleton
class SchedulingConcurrencyLockManager @Inject constructor() {

    companion object {
        const val DEFAULT_HOLD_DURATION_MINUTES = 10
    }

    private val reservations = mutableMapOf<String, TimeSlotReservation>()

    /**
     * Tenta adquirir a trava exclusiva de concorrência para o horário solicitado.
     *
     * @param professionalId Identificador do profissional prestador.
     * @param serviceId Identificador do serviço.
     * @param slotIsoDateTime Horário pretendido em ISO-8601.
     * @param userId Identificador do cliente.
     * @param currentTimeMillis Carimbo de data/hora atual.
     * @param holdMinutes Duração da exclusividade temporária em minutos (padrão: 10).
     * @return [ReservationOutcome.Acquired] ou [ReservationOutcome.Conflict].
     */
    fun acquireSlotLock(
        professionalId: String,
        serviceId: String,
        slotIsoDateTime: String,
        userId: String,
        currentTimeMillis: Long = System.currentTimeMillis(),
        holdMinutes: Int = DEFAULT_HOLD_DURATION_MINUTES
    ): ReservationOutcome {
        val slotKey = "$professionalId@@@$slotIsoDateTime"
        val existing = reservations[slotKey]

        if (existing != null) {
            when {
                existing.status == SlotReservationStatus.CONFIRMED -> {
                    return ReservationOutcome.Conflict("Este horário já foi agendado e confirmado por outro cliente.")
                }
                existing.status == SlotReservationStatus.RESERVED_ACTIVE -> {
                    if (currentTimeMillis < existing.expiresAtMillis) {
                        if (existing.reservedByUserId != userId) {
                            return ReservationOutcome.Conflict("Este horário está temporariamente reservado por outro cliente. Tente novamente em instantes.")
                        } else {
                            // Mesmo usuário: renova o lock
                            val renewed = existing.copy(
                                expiresAtMillis = currentTimeMillis + (holdMinutes * 60 * 1000L)
                            )
                            reservations[slotKey] = renewed
                            return ReservationOutcome.Acquired(renewed, holdMinutes)
                        }
                    }
                    // Se expirou, prossegue para conceder a trava ao novo solicitante
                }
                else -> {}
            }
        }

        val expiresAt = currentTimeMillis + (holdMinutes * 60 * 1000L)
        val newReservation = TimeSlotReservation(
            reservationId = UUID.randomUUID().toString(),
            professionalId = professionalId,
            serviceId = serviceId,
            slotIsoDateTime = slotIsoDateTime,
            reservedByUserId = userId,
            expiresAtMillis = expiresAt,
            status = SlotReservationStatus.RESERVED_ACTIVE
        )

        reservations[slotKey] = newReservation
        return ReservationOutcome.Acquired(newReservation, holdMinutes)
    }

    /**
     * Efetiva e confirma definitivamente a vaga após aprovação do pagamento.
     *
     * @param reservationId Identificador da reserva temporária.
     * @return true se a vaga foi confirmada com sucesso.
     */
    fun confirmReservation(reservationId: String): Boolean {
        val entry = reservations.entries.find { it.value.reservationId == reservationId } ?: return false
        if (entry.value.status != SlotReservationStatus.RESERVED_ACTIVE) return false

        reservations[entry.key] = entry.value.copy(status = SlotReservationStatus.CONFIRMED)
        return true
    }

    /**
     * Libera voluntariamente a trava temporária caso o cliente desista da escolha antes do timeout.
     *
     * @param reservationId Identificador da reserva a ser liberada.
     * @return true se a vaga foi liberada com sucesso.
     */
    fun releaseSlotLock(reservationId: String): Boolean {
        val entry = reservations.entries.find { it.value.reservationId == reservationId } ?: return false
        reservations[entry.key] = entry.value.copy(status = SlotReservationStatus.RELEASED)
        return true
    }

    /**
     * Remove ou marca como expiradas todas as reservas cujo prazo de hold tenha se esgotado.
     *
     * @param currentTimeMillis Carimbo de data/hora atual.
     * @return Quantidade de vagas liberadas de volta à agenda.
     */
    fun cleanExpiredLocks(currentTimeMillis: Long = System.currentTimeMillis()): Int {
        var freedCount = 0
        for ((key, res) in reservations.entries) {
            if (res.status == SlotReservationStatus.RESERVED_ACTIVE && currentTimeMillis >= res.expiresAtMillis) {
                reservations[key] = res.copy(status = SlotReservationStatus.EXPIRED)
                freedCount++
            }
        }
        return freedCount
    }
}
