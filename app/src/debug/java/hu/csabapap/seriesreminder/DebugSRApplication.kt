package hu.csabapap.seriesreminder

import com.facebook.stetho.Stetho
import timber.log.Timber

class DebugSRApplication : SRApplication() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        Timber.plant(Timber.DebugTree())
    }
}