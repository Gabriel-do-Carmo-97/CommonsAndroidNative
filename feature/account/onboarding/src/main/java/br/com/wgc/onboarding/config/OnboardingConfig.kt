package br.com.wgc.onboarding.config

/**
 * Configurações de comportamento e apresentação visual da experiência de Onboarding.
 *
 * @property enableLottieAnimations Indica se animações Lottie ricas devem ser executadas nos slides.
 * @property autoRequestPermissions Indica se permissões de sistema devem ser solicitadas automaticamente ao final.
 */
data class OnboardingConfig(
    val enableLottieAnimations: Boolean = true,
    val autoRequestPermissions: Boolean = false
)