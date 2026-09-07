package br.com.gds.commonsandroidnative

import android.app.Application
import br.wgc.omnibackend.firebase.OmniFirebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CommonsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        OmniFirebase.initialize(this, enableAppCheck = false, isDebug = true)
    }
}
