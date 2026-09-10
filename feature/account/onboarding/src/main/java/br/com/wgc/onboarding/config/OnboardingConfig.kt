package br.com.wgc.onboarding.config

/**
 * ConfiguraÃ§Ãµes de comportamento e apresentaÃ§Ã£o visual da experiÃªncia de Onboarding.
 *
 * @property enableLottieAnimations Indica se animaÃ§Ãµes Lottie ricas devem ser executadas nos slides.
 * @property autoRequestPermissions Indica se permissÃµes de sistema devem ser solicitadas automaticamente ao final.
 */
data class OnboardingConfig(
    val enableLottieAnimations: Boolean = true,
    val autoRequestPermissions: Boolean = false
)