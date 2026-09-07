package br.com.gds.stores.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.wgc.omnibackend.core.repository.FirestoreRepository
import br.wgc.omnibackend.core.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreBranch(
    val id: String,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val isOpen: Boolean = true,
    val phone: String = ""
)

data class StoresUiState(
    val title: String = "Módulo de Multi-Lojas e Filiais WGC",
    val isLoading: Boolean = false,
    val stores: List<StoreBranch> = emptyList(),
    val filteredStores: List<StoreBranch> = emptyList(),
    val selectedStore: StoreBranch? = null,
    val searchQuery: String = "",
    val onlyOpenFilter: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class StoresViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoresUiState())
    val uiState: StateFlow<StoresUiState> = _uiState.asStateFlow()

    init {
        loadStores()
    }

    fun loadStores() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = firestoreRepository.findDocuments(
                collection = "stores",
                filters = emptyList(),
                clazz = Map::class.java
            )
            when (result) {
                is DataResult.Success -> {
                    val remoteStores = result.data.mapNotNull { doc ->
                        val map = doc as? Map<*, *> ?: return@mapNotNull null
                        val id = map["id"] as? String ?: return@mapNotNull null
                        val name = map["name"] as? String ?: "Loja WGC"
                        val address = map["address"] as? String ?: ""
                        val distance = (map["distanceKm"] as? Number)?.toDouble() ?: 1.5
                        val isOpen = map["isOpen"] as? Boolean ?: true
                        val phone = map["phone"] as? String ?: ""
                        StoreBranch(id, name, address, distance, isOpen, phone)
                    }

                    val finalStores = remoteStores.ifEmpty { defaultStores }
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            stores = finalStores,
                            filteredStores = applyFilter(finalStores, current.searchQuery, current.onlyOpenFilter)
                        )
                    }
                }
                is DataResult.Failure -> {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            stores = defaultStores,
                            filteredStores = applyFilter(defaultStores, current.searchQuery, current.onlyOpenFilter)
                        )
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredStores = applyFilter(current.stores, query, current.onlyOpenFilter)
            )
        }
    }

    fun onToggleOnlyOpen(onlyOpen: Boolean) {
        _uiState.update { current ->
            current.copy(
                onlyOpenFilter = onlyOpen,
                filteredStores = applyFilter(current.stores, current.searchQuery, onlyOpen)
            )
        }
    }

    fun selectStore(store: StoreBranch) {
        _uiState.update { it.copy(selectedStore = store) }
    }

    private fun applyFilter(list: List<StoreBranch>, query: String, onlyOpen: Boolean): List<StoreBranch> {
        return list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.address.contains(query, ignoreCase = true)
            val matchesOpen = !onlyOpen || item.isOpen
            matchesQuery && matchesOpen
        }
    }

    companion object {
        val defaultStores = listOf(
            StoreBranch("store_1", "WGC Flagship - Av. Paulista", "Av. Paulista, 1578 - Bela Vista, SP", 0.8, true, "(11) 3100-0001"),
            StoreBranch("store_2", "WGC Concept - Jardins", "Rua Oscar Freire, 900 - Cerqueira César, SP", 2.3, true, "(11) 3100-0002"),
            StoreBranch("store_3", "WGC Express - Faria Lima", "Av. Brg. Faria Lima, 2200 - Pinheiros, SP", 4.1, false, "(11) 3100-0003"),
            StoreBranch("store_4", "WGC Hub - Barra da Tijuca", "Av. das Américas, 4666 - Barra, RJ", 12.0, true, "(21) 2400-0001")
        )
    }
}
