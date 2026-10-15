package br.com.wgc.multi_language.locale

import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [DynamicLocaleManager].
 */
class DynamicLocaleManagerTest {

    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var localeManager: DynamicLocaleManager

    @Before
    fun setup() {
        storage = InMemoryKeyValueStorage()
        localeManager = DynamicLocaleManager(storage)
    }

    @Test
    fun `inicializacao padrao deve definir PORTUGUESE`() = runTest {
        assertEquals(AppLanguage.PORTUGUESE, localeManager.currentLanguage.first())
        assertEquals("pt", localeManager.getActiveJavaLocale().language)
        assertEquals("BR", localeManager.getActiveJavaLocale().country)
    }

    @Test
    fun `setLanguage para ENGLISH deve atualizar StateFlow e persistir tag`() = runTest {
        localeManager.setLanguage(AppLanguage.ENGLISH)

        assertEquals(AppLanguage.ENGLISH, localeManager.currentLanguage.first())
        assertEquals("en-US", storage.getString("wgc_selected_language_tag"))
        assertEquals("en", localeManager.getActiveJavaLocale().language)
        assertEquals("US", localeManager.getActiveJavaLocale().country)
    }

    @Test
    fun `setLanguage para SPANISH deve atualizar StateFlow e persistir tag`() = runTest {
        localeManager.setLanguage(AppLanguage.SPANISH)

        assertEquals(AppLanguage.SPANISH, localeManager.currentLanguage.first())
        assertEquals("es-ES", storage.getString("wgc_selected_language_tag"))
        assertEquals("es", localeManager.getActiveJavaLocale().language)
        assertEquals("ES", localeManager.getActiveJavaLocale().country)
    }

    @Test
    fun `inicializacao com tag previamente salva deve restaurar idioma correto`() = runTest {
        storage.saveString("wgc_selected_language_tag", "en-US")
        val restoredManager = DynamicLocaleManager(storage)

        assertEquals(AppLanguage.ENGLISH, restoredManager.currentLanguage.first())
    }

    @Test
    fun `AppLanguage fromTag com tag desconhecida deve retornar PORTUGUESE por padrao`() {
        val fallback = AppLanguage.fromTag("invalid-tag-123")
        assertEquals(AppLanguage.PORTUGUESE, fallback)
    }
}
