package br.com.gds.maps.navigation

sealed interface MapsNavDestinations {
    data object MapOverview : MapsNavDestinations
    data class AddressDetails(val addressId: String) : MapsNavDestinations
}
