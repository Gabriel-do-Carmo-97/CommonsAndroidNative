package br.com.wgc.driver_app.proof

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [DeliveryProofValidator] validando regras de OTP, verificação fotográfica e dados de entrega.
 */
class DeliveryProofValidatorTest {

    private lateinit var validator: DeliveryProofValidator

    @Before
    fun setup() {
        validator = DeliveryProofValidator()
    }

    @Test
    fun `validateProof with matching OTP and valid recipient succeeds`() {
        val submission = DeliveryProofSubmission(
            orderId = "ord-1",
            otpEntered = "5921",
            recipientName = "Carlos Mendes"
        )

        val result = validator.validateProof(
            expectedOtp = "5921",
            requiredType = DeliveryProofType.OTP_CODE,
            submission = submission
        )

        assertTrue(result is ProofValidationResult.Success)
        assertEquals("ord-1", (result as ProofValidationResult.Success).orderId)
    }

    @Test
    fun `validateProof with wrong OTP fails with otp field failure`() {
        val submission = DeliveryProofSubmission(
            orderId = "ord-2",
            otpEntered = "1234",
            recipientName = "Maria Souza"
        )

        val result = validator.validateProof(
            expectedOtp = "9999",
            requiredType = DeliveryProofType.OTP_CODE,
            submission = submission
        )

        assertTrue(result is ProofValidationResult.Failure)
        val failure = result as ProofValidationResult.Failure
        assertEquals("otp", failure.failedField)
        assertTrue(failure.reason.contains("incorreto"))
    }

    @Test
    fun `validateProof with PHOTO_CONFIRMATION requires valid image extension`() {
        val submissionInvalidExt = DeliveryProofSubmission(
            orderId = "ord-3",
            photoFilePath = "/storage/emulated/0/photo.pdf",
            recipientName = "Portaria"
        )

        val result = validator.validateProof(
            expectedOtp = null,
            requiredType = DeliveryProofType.PHOTO_CONFIRMATION,
            submission = submissionInvalidExt
        )

        assertTrue(result is ProofValidationResult.Failure)
        assertEquals("photo", (result as ProofValidationResult.Failure).failedField)

        val submissionValidExt = DeliveryProofSubmission(
            orderId = "ord-3",
            photoFilePath = "/storage/emulated/0/delivery_proof.jpg",
            recipientName = "Portaria"
        )

        val successResult = validator.validateProof(
            expectedOtp = null,
            requiredType = DeliveryProofType.PHOTO_CONFIRMATION,
            submission = submissionValidExt
        )
        assertTrue(successResult is ProofValidationResult.Success)
    }

    @Test
    fun `validateProof with empty recipient name fails`() {
        val submission = DeliveryProofSubmission(
            orderId = "ord-4",
            otpEntered = "1111",
            recipientName = ""
        )

        val result = validator.validateProof(
            expectedOtp = "1111",
            requiredType = DeliveryProofType.OTP_CODE,
            submission = submission
        )

        assertTrue(result is ProofValidationResult.Failure)
        assertEquals("recipient", (result as ProofValidationResult.Failure).failedField)
    }
}
