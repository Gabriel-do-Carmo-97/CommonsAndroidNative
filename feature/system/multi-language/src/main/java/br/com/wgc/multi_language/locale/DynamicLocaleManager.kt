package br.com.wgc.multi_language.locale

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import br.com.wgc.core.sharedPreferences.KeyValueStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Idiomas oficiais suportados pelo ecossistema WGC.
 *
 * @property languageTag Tag IETF BCP 47 (ex: "pt-BR", "en-US", "es-ES").
 * @property languageCode Código ISO 639-1 de 2 letras.
 * @property countryCode Código ISO 3166-1 alpha-2 do país.
 * @property displayName Nome amigável para exibição em seletores de UI.
 */
enum class AppLanguage(
    val languageTag: String,
    val languageCode: String,
    val countryCode: String,
    val displayName: String
) {
    PORTUGUESE("pt-BR", "pt", "BR", "Português (Brasil)"),
    ENGLISH("en-US", "en", "US", "English (United States)"),
    SPANISH("es-ES", "es", "ES", "Español");

    companion object {
        /**
         * Localiza o [AppLanguage] correspondente à tag informada, com fallback para [PORTUGUESE].
         *
         * @param tag Tag de linguagem IETF ou código ISO.
         * @return [AppLanguage] identificado.
         */
        fun fromTag(tag: String?): AppLanguage {
            return entries.firstOrNull { 
                it.languageTag.equals(tag, ignoreCase = true) || 
                it.languageCode.equals(tag, ignoreCase = true) 
            } ?: PORTUGUESE
        }
    }
}

/**
 * Gerenciador corporativo de internacionalização e troca dinâmica de localidade em tempo de execução.
 *
 * Permite alternar o idioma da interface em runtime sem necessidade de reinicialização forçada do processo,
 * persistindo a preferência no [KeyValueStorage] e injetando o novo contexto configurado.
 *
 * ### 📌 Quando Usar:
 * - Em telas de configurações do usuário para seleção de idioma do aplicativo.
 * - Na inicialização do app (Application/Activity) para restaurar a localidade escolhida pelo usuário.
 * - Em fluxos multi-idioma (Português, Inglês e Espanhol) nos módulos de comércio, logística e finanças.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // Injetado via Hilt:
 * @Inject lateinit var localeManager: DynamicLocaleManager
 *
 * // 1. Observar idioma ativo na UI:
 * val currentLang by localeManager.currentLanguage.collectAsState()
 *
 * // 2. Trocar idioma instantaneamente:
 * localeManager.setLanguage(AppLanguage.ENGLISH)
 *
 * // 3. Aplicar contexto configurado na Activity.attachBaseContext:
 * override fun attachBaseContext(newBase: Context) {
 *     super.attachBaseContext(localeManager.wrapContext(newBase))
 * }
 * ```
 */
@Singleton
class DynamicLocaleManager @Inject constructor(
    private val storage: KeyValueStorage
) {
    companion object {
        private const val KEY_SELECTED_LANGUAGE = "wgc_selected_language_tag"
    }

    private val _currentLanguage = MutableStateFlow(loadInitialLanguage())

    /** Fluxo reativo do idioma atualmente selecionado. */
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    /**
     * Define e persiste o novo idioma de execução do aplicativo.
     *
     * @param language [AppLanguage] desejado para a interface.
     */
    fun setLanguage(language: AppLanguage) {
        storage.saveString(KEY_SELECTED_LANGUAGE, language.languageTag)
        Locale.setDefault(createJavaLocale(language))
        _currentLanguage.value = language
    }

    /**
     * Envolve o contexto Android fornecido com a [Configuration] contendo a localidade ativa.
     *
     * @param context Contexto base do Android.
     * @return Novo [Context] configurado com a localidade ativa.
     */
    fun wrapContext(context: Context): Context {
        val locale = createJavaLocale(_currentLanguage.value)
        val config = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        return context.createConfigurationContext(config)
    }

    /**
     * Retorna a instância nativa de [Locale] para o [AppLanguage] ativo.
     *
     * @return [Locale] Java/Android configurado.
     */
    fun getActiveJavaLocale(): Locale {
        return createJavaLocale(_currentLanguage.value)
    }

    private fun loadInitialLanguage(): AppLanguage {
        val savedTag = storage.getString(KEY_SELECTED_LANGUAGE)
        return if (!savedTag.isNullOrBlank()) {
            AppLanguage.fromTag(savedTag)
        } else {
            AppLanguage.PORTUGUESE
        }
    }

    private fun createJavaLocale(language: AppLanguage): Locale {
        return Locale(language.languageCode, language.countryCode)
    }
}
