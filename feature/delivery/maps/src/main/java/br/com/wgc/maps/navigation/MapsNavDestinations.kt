package br.com.wgc.maps.navigation

sealed interface MapsNavDestinations {
    data object MapOverview : MapsNavDestinations
    data class AddressDetails(val addressId: String) : MapsNavDestinations
    data class LiveTracking(val entityId: String, val entityType: String = "drivers") : MapsNavDestinations
}
