package br.com.wgc.commonsandroidnative.network

import br.com.wgc.core.network.security.DynamicCertificatePinner
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.CertificatePinner
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Suíte de testes unitários para [NetworkSecurityConfig].
 *
 * Valida o registro de hashes SSL/TLS padrão e a compilação do [CertificatePinner].
 */
class NetworkSecurityConfigTest {

    private val dynamicCertificatePinner = mockk<DynamicCertificatePinner>(relaxed = true)

    @Test
    fun `NetworkSecurityConfig registers default corporate pins upon initialization`() {
        val mockPinner = CertificatePinner.DEFAULT
        every { dynamicCertificatePinner.buildCertificatePinner() } returns mockPinner

        val config = NetworkSecurityConfig(dynamicCertificatePinner)

        verify { dynamicCertificatePinner.addPins("*.wgc.com.br", any(), any()) }
        verify { dynamicCertificatePinner.addPins("firestore.googleapis.com", any(), any()) }
        verify { dynamicCertificatePinner.addPins("identitytoolkit.googleapis.com", any(), any()) }

        val pinner = config.buildCertificatePinner()
        assertNotNull(pinner)
    }

    @Test
    fun `addPins delegates registration to dynamicCertificatePinner`() {
        val config = NetworkSecurityConfig(dynamicCertificatePinner)

        config.addPins("custom.domain.com", "sha256/testpin123=")

        verify { dynamicCertificatePinner.addPins("custom.domain.com", "sha256/testpin123=") }
    }
}
