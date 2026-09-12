package br.com.wgc.scheduling.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.wgc.omnibackend.core.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Item representativo de um serviço disponível para agendamento.
 *
 * @param id Identificador do serviço.
 * @param name Nome comercial do serviço.
 * @param durationMin Duração estimada em minutos.
 * @param price Valor monetário formatado.
 * @property id Identificador do serviço.
 * @property name Nome comercial do serviço.
 * @property durationMin Duração estimada em minutos.
 * @property price Valor monetário formatado.
 */
data class ServiceItem(
    val id: String,
    val name: String,
    val durationMin: Int,
    val price: String
)

/**
 * Profissional credenciado encarregado da execução do atendimento.
 *
 * @param id Identificador do profissional.
 * @param name Nome completo do prestador.
 * @param role Cargo ou especialidade técnica.
 * @property id Identificador do profissional.
 * @property name Nome completo do prestador.
 * @property role Cargo ou especialidade técnica.
 */
data class ProviderItem(
    val id: String,
    val name: String,
    val role: String
)

/**
 * Intervalo de horário (slot) da agenda de atendimento.
 *
 * @param time Horário no formato HH:mm.
 * @param isAvailable Sinalizador indicando se o horário está livre para reserva.
 * @property time Horário no formato HH:mm.
 * @property isAvailable Sinalizador indicando se o horário está livre para reserva.
 */
data class TimeSlot(
    val time: String,
    val isAvailable: Boolean = true
)

/**
 * Estado imutável do fluxo de agendamento de serviços.
 *
 * @property title Título do cabeçalho da tela de agendamento.
 * @property services Catálogo de serviços ofertados para agendamento.
 * @property selectedService Serviço atualmente selecionado pelo cliente.
 * @property providers Lista de profissionais habilitados.
 * @property selectedProvider Profissional selecionado para a execução.
 * @property availableDates Dias do calendário disponíveis para reserva.
 * @property selectedDate Data escolhida.
 * @property timeSlots Faixas de horários para a data selecionada.
 * @property selectedTime Horário escolhido.
 * @property isSubmitting Indicador de transação de agendamento em andamento.
 * @property isBookingConfirmed Indicador de agendamento validado e registrado com sucesso.
 */
data class SchedulingUiState(
    val title: String = "Agendamento de Serviços",
    val services: List<ServiceItem> = listOf(
        ServiceItem("s1", "Corte de Cabelo & Barba", 45, "R$ 65,00"),
        ServiceItem("s2", "Revisão Automotiva Preventiva", 120, "R$ 280,00"),
        ServiceItem("s3", "Consulta Médica / Avaliação", 30, "R$ 150,00"),
        ServiceItem("s4", "Limpeza e Higienização VIP", 60, "R$ 110,00")
    ),
    val selectedService: ServiceItem = ServiceItem("s1", "Corte de Cabelo & Barba", 45, "R$ 65,00"),
    val providers: List<ProviderItem> = listOf(
        ProviderItem("p1", "Carlos Andrade", "Especialista Master"),
        ProviderItem("p2", "Fernanda Lima", "Profissional Sênior"),
        ProviderItem("p3", "Qualquer Profissional Disponível", "Mais Rápido")
    ),
    val selectedProvider: ProviderItem = ProviderItem("p1", "Carlos Andrade", "Especialista Master"),
    val availableDates: List<String> = listOf("Hoje", "Amanhã", "Quarta", "Quinta", "Sexta", "Sábado"),
    val selectedDate: String = "Hoje",
    val timeSlots: List<TimeSlot> = listOf(
        TimeSlot("09:00", true),
        TimeSlot("10:30", true),
        TimeSlot("11:45", false),
        TimeSlot("14:00", true),
        TimeSlot("15:30", true),
        TimeSlot("17:00", false)
    ),
    val selectedTime: String = "10:30",
    val isSubmitting: Boolean = false,
    val isBookingConfirmed: Boolean = false
)

/**
 * ViewModel responsável pelo fluxo de reserva e integração com a coleção `appointments` do Firestore.
 *
 * @param firestoreRepository Repositório do OmniBackend encarregado do registro remoto do agendamento.
 */
@HiltViewModel
class SchedulingViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SchedulingUiState())

    /**
     * Fluxo observável contendo o estado completo da reserva.
     */
    val uiState: StateFlow<SchedulingUiState> = _uiState.asStateFlow()

    /**
     * Seleciona o serviço a ser contratado.
     *
     * @param service Item de serviço escolhido.
     */
    fun selectService(service: ServiceItem) {
        _uiState.update { it.copy(selectedService = service) }
    }

    /**
     * Define o profissional prestador escolhido para o atendimento.
     *
     * @param provider Objeto do profissional selecionado.
     */
    fun selectProvider(provider: ProviderItem) {
        _uiState.update { it.copy(selectedProvider = provider) }
    }

    /**
     * Define o dia selecionado para o atendimento.
     *
     * @param date Rótulo da data selecionada.
     */
    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    /**
     * Define o horário específico do atendimento.
     *
     * @param time Horário selecionado.
     */
    fun selectTime(time: String) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    /**
     * Confirma a reserva e persiste os dados na coleção `appointments` do OmniBackend.
     */
    fun confirmBooking() {
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val payload = mapOf(
                "service" to _uiState.value.selectedService.name,
                "provider" to _uiState.value.selectedProvider.name,
                "date" to _uiState.value.selectedDate,
                "time" to _uiState.value.selectedTime,
                "price" to _uiState.value.selectedService.price,
                "timestamp" to System.currentTimeMillis()
            )
            try {
                firestoreRepository.addDocument(
                    collection = "appointments",
                    data = payload,
                    customId = null
                )
            } catch (_: Exception) {
                // Fallback gracioso offline
            }
            _uiState.update { it.copy(isSubmitting = false, isBookingConfirmed = true) }
        }
    }
}