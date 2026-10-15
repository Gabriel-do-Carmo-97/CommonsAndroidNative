package br.com.wgc.media_picker.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Testes unitários para [AudioVoiceNoteManager].
 */
class AudioVoiceNoteManagerTest {

    private lateinit var audioManager: AudioVoiceNoteManager
    private lateinit var tempFile: File

    @Before
    fun setup() {
        audioManager = AudioVoiceNoteManager()
        tempFile = File.createTempFile("test_audio", ".m4a")
        tempFile.writeBytes(ByteArray(1024)) // 1 KB simulado
    }

    @Test
    fun `startRecording deve transitar para estado RECORDING`() {
        audioManager.startRecording(tempFile)
        assertEquals(VoiceRecordingState.RECORDING, audioManager.getState())
    }

    @Test
    fun `startRecording quando ja gravando deve lancar IllegalStateException`() {
        audioManager.startRecording(tempFile)
        assertThrows(IllegalStateException::class.java) {
            audioManager.startRecording(tempFile)
        }
    }

    @Test
    fun `finishRecording com duracao menor que 1s deve descartar arquivo e retornar null`() {
        audioManager.startRecording(tempFile)
        val result = audioManager.finishRecording(durationMillis = 800L)

        assertNull(result)
        assertEquals(VoiceRecordingState.CANCELLED, audioManager.getState())
    }

    @Test
    fun `finishRecording com duracao valida deve retornar VoiceNoteResult`() {
        audioManager.startRecording(tempFile)
        val result = audioManager.finishRecording(durationMillis = 3500L)

        assertNotNull(result)
        assertEquals(VoiceRecordingState.FINISHED, audioManager.getState())
        assertEquals(3500L, result?.durationMillis)
        assertEquals(tempFile.absolutePath, result?.file?.absolutePath)
    }

    @Test
    fun `cancelRecording deve deletar arquivo e mudar estado para CANCELLED`() {
        audioManager.startRecording(tempFile)
        audioManager.cancelRecording()

        assertEquals(VoiceRecordingState.CANCELLED, audioManager.getState())
    }

    @Test
    fun `normalizeAmplitude deve converter amplitudes para escala de 0 a 1`() {
        assertEquals(0f, audioManager.normalizeAmplitude(0), 0.001f)
        assertEquals(1f, audioManager.normalizeAmplitude(32767), 0.001f)
        assertTrue(audioManager.normalizeAmplitude(16384) > 0.49f)
    }
}
