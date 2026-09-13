package br.com.wgc.commonsandroidnative

import android.app.Application
import br.com.wgc.telemetry.boundary.WgcGlobalExceptionHandler
import br.wgc.omnibackend.firebase.OmniFirebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CommonsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WgcGlobalExceptionHandler.initialize(this)
        OmniFirebase.initialize(this, enableAppCheck = false, isDebug = true)
    }
}
