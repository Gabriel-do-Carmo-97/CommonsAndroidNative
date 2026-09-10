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

data class ServiceItem(val id: String, val name: String, val durationMin: Int, val price: String)
data class ProviderItem(val id: String, val name: String, val role: String)
data class TimeSlot(val time: String, val isAvailable: Boolean = true)

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

@HiltViewModel
class SchedulingViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SchedulingUiState())
    val uiState: StateFlow<SchedulingUiState> = _uiState.asStateFlow()

    fun selectService(service: ServiceItem) {
        _uiState.update { it.copy(selectedService = service) }
    }

    fun selectProvider(provider: ProviderItem) {
        _uiState.update { it.copy(selectedProvider = provider) }
    }

    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun selectTime(time: String) {
        _uiState.update { it.copy(selectedTime = time) }
    }

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
