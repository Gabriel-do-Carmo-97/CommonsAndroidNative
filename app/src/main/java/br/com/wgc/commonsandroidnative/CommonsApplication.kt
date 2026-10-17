package br.com.wgc.commonsandroidnative

import android.app.Application
import br.com.wgc.telemetry.boundary.WgcGlobalExceptionHandler
import br.wgc.omnibackend.firebase.OmniFirebase
import dagger.hilt.android.HiltAndroidApp

/**
 * Ponto central de inicialização da aplicação Android Commons (CommonsAndroidNative).
 *
 * Configura o gerenciador global de exceções e a camada unificada de backend [OmniFirebase],
 * garantindo fallback resiliente para ambiente local/desenvolvimento caso o arquivo de credenciais
 * `google-services.json` não esteja presente no runtime.
 */
@HiltAndroidApp
class CommonsApplication : Application() {

    /**
     * Ciclo de inicialização chamado quando o processo da aplicação é instanciado.
     *
     * Inicializa o rastreamento global de erros, o contexto padrão do Firebase com opções de desenvolvimento
     * em sandbox e o provedor [OmniFirebase].
     */
    override fun onCreate() {
        super.onCreate()
        WgcGlobalExceptionHandler.initialize(this)
        initializeFirebaseSafely()
        OmniFirebase.initialize(this, enableAppCheck = false, isDebug = true)
    }

    /**
     * Inicializa a instância default do FirebaseApp com credenciais simuladas para ambiente de sandbox/showcase
     * caso o aplicativo não tenha sido provisionado via arquivo `google-services.json`.
     *
     * Utiliza reflexão para desacoplar a compilação direta do módulo app das classes de infraestrutura do Firebase.
     */
    private fun initializeFirebaseSafely() {
        try {
            val firebaseAppClass = Class.forName("com.google.firebase.FirebaseApp")
            val getAppsMethod = firebaseAppClass.getMethod("getApps", android.content.Context::class.java)
            val apps = getAppsMethod.invoke(null, this) as? List<*>
            if (apps.isNullOrEmpty()) {
                val firebaseOptionsClass = Class.forName("com.google.firebase.FirebaseOptions")
                val builderClass = Class.forName("com.google.firebase.FirebaseOptions\$Builder")
                val builderInstance = builderClass.getDeclaredConstructor().newInstance()

                builderClass.getMethod("setApplicationId", String::class.java)
                    .invoke(builderInstance, "1:123456789012:android:commonsdummyapp")
                builderClass.getMethod("setApiKey", String::class.java)
                    .invoke(builderInstance, "AIzaSyDummyKeyForCommonsNativeDev12345")
                builderClass.getMethod("setProjectId", String::class.java)
                    .invoke(builderInstance, "commons-android-native")
                builderClass.getMethod("setDatabaseUrl", String::class.java)
                    .invoke(builderInstance, "https://commons-android-native-default-rtdb.firebaseio.com")
                builderClass.getMethod("setStorageBucket", String::class.java)
                    .invoke(builderInstance, "commons-android-native.appspot.com")

                val options = builderClass.getMethod("build").invoke(builderInstance)
                firebaseAppClass.getMethod("initializeApp", android.content.Context::class.java, firebaseOptionsClass)
                    .invoke(null, this, options)
            }
        } catch (_: Throwable) {
            // Continua a execução caso o Firebase não esteja disponível no runtime
        }
    }
}


