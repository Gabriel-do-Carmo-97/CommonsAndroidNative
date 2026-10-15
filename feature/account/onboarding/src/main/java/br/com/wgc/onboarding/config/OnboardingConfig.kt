package br.com.wgc.onboarding.config

/**
 * Configurações de comportamento e apresentação visual da experiência de Onboarding.
 *
 * Controla a ativação de animações ricas em Lottie nos slides de apresentação
 * e a solicitação automática de permissões de sistema ao concluir o fluxo.
 *
 * ### 📌 Quando Usar:
 * Utilize para customizar dinamicamente a apresentação do Onboarding de acordo com o sabor (flavor)
 * do aplicativo ou preferências remotas via Feature Flags / Remote Config.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val config = OnboardingConfig(
 *     enableLottieAnimations = true,
 *     autoRequestPermissions = true
 * )
 * ```
 *
 * @property enableLottieAnimations Indica se animações Lottie ricas devem ser executadas nos slides.
 * @property autoRequestPermissions Indica se permissões de sistema (geolocalização, notificações) devem ser solicitadas automaticamente ao final.
 */
data class OnboardingConfig(
    val enableLottieAnimations: Boolean = true,
    val autoRequestPermissions: Boolean = false
)