package br.com.wgc.authentication.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [ValidateAuthCredentialsUseCase].
 */
class ValidateAuthCredentialsUseCaseTest {

    private lateinit var useCase: ValidateAuthCredentialsUseCase

    @Before
    fun setup() {
        useCase = ValidateAuthCredentialsUseCase()
    }

    @Test
    fun `invoke com email nulo ou invalido deve retornar InvalidEmail`() {
        val result1 = useCase(null, "Pass12345")
        assertTrue(result1 is CredentialsValidationResult.InvalidEmail)

        val result2 = useCase("email_sem_arroba", "Pass12345")
        assertTrue(result2 is CredentialsValidationResult.InvalidEmail)
    }

    @Test
    fun `invoke com senha menor que 8 digitos deve retornar WeakPassword`() {
        val result = useCase("user@test.com", "P123")
        assertTrue(result is CredentialsValidationResult.WeakPassword)
        assertEquals("A senha deve conter no mínimo 8 caracteres.", (result as CredentialsValidationResult.WeakPassword).message)
    }

    @Test
    fun `invoke com senha sem letras deve retornar WeakPassword`() {
        val result = useCase("user@test.com", "12345678")
        assertTrue(result is CredentialsValidationResult.WeakPassword)
        assertEquals("A senha deve conter pelo menos uma letra e um número.", (result as CredentialsValidationResult.WeakPassword).message)
    }

    @Test
    fun `invoke com senha sem numeros deve retornar WeakPassword`() {
        val result = useCase("user@test.com", "abcdefgh")
        assertTrue(result is CredentialsValidationResult.WeakPassword)
        assertEquals("A senha deve conter pelo menos uma letra e um número.", (result as CredentialsValidationResult.WeakPassword).message)
    }

    @Test
    fun `invoke com credenciais solidas deve retornar Valid`() {
        val result = useCase("contato@empresa.com.br", "MinhaSenha123")
        assertTrue(result is CredentialsValidationResult.Valid)
    }
}
