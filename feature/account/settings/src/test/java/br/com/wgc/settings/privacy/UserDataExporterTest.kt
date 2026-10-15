package br.com.wgc.settings.privacy

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [UserDataExporter] validando geração do dossiê JSON e integridade das chaves.
 */
class UserDataExporterTest {

    private lateinit var exporter: UserDataExporter

    @Before
    fun setup() {
        exporter = UserDataExporter()
    }

    @Test
    fun `generateExportJson produces valid JSON with profile, addresses and consents`() {
        val bundle = UserDataExportBundle(
            profile = UserPersonalProfile(
                userId = "u-101",
                fullName = "Gabriel do Carmo",
                email = "gabriel@test.com",
                phone = "(11) 99999-0000",
                createdAt = "2026-01-15T10:00:00Z"
            ),
            addresses = listOf(
                UserAddressRecord(
                    id = "addr-1",
                    street = "Av. Paulista",
                    number = "1000",
                    zipCode = "01310-100",
                    city = "São Paulo",
                    state = "SP"
                )
            ),
            orders = listOf(
                UserOrderRecord(
                    orderId = "ord-99",
                    date = "2026-10-14",
                    totalAmount = "R$ 150,00",
                    itemsCount = 3
                )
            ),
            privacyConsents = PrivacyConsentPreferences(
                analyticsAccepted = true,
                marketingAccepted = false,
                policyVersion = "1.0"
            ),
            exportedAt = "2026-10-15T12:00:00Z"
        )

        val json = exporter.generateExportJson(bundle)

        assertTrue(json.contains("Gabriel do Carmo"))
        assertTrue(json.contains("gabriel@test.com"))
        assertTrue(json.contains("Av. Paulista"))
        assertTrue(json.contains("ord-99"))
        assertTrue(json.contains("LGPD Lei 13.709/2018"))

        assertTrue(exporter.validateExportIntegrity(json))
    }
}
