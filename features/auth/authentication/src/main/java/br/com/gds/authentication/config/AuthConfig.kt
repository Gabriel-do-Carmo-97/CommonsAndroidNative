package br.com.gds.authentication.config

/**
 * Objeto de configuração declarativo que permite aos apps consumidores customizarem
 * o fluxo de autenticação de acordo com as necessidades específicas do negócio.
 *
 * @param enableCarRegistration Se verdadeiro, exibe e permite a etapa de cadastro de veículo.
 * @param enableAddressRegistration Se verdadeiro, inclui a etapa de cadastro de endereço.
 * @param enableBiometrics Se verdadeiro, habilita atalho para autenticação biométrica quando disponível.
 * @param isClient Define o perfil padrão do usuário registrado (ex: true para Cliente, false para Motorista/Parceiro).
 * @param termsOfServiceUrl URL opcional para abrir os Termos de Uso.
 * @param privacyPolicyUrl URL opcional para a Política de Privacidade.
 */
data class AuthConfig(
    val enableCarRegistration: Boolean = false,
    val enableAddressRegistration: Boolean = true,
    val enableBiometrics: Boolean = true,
    val isClient: Boolean = true,
    val termsOfServiceUrl: String? = null,
    val privacyPolicyUrl: String? = null
)
