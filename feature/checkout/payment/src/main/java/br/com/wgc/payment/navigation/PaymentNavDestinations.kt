package br.com.wgc.payment.navigation

sealed interface PaymentNavDestinations {
    data object Checkout : PaymentNavDestinations
    data class PaymentHistory(val userId: String) : PaymentNavDestinations
}
