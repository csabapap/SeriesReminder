package hu.csabapap.seriesreminder

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import hu.csabapap.seriesreminder.inject.AppComponent
import hu.csabapap.seriesreminder.inject.DaggerAppComponent

open class SRApplication : DaggerApplication() {

    private lateinit var appComponent : AppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build()
        return appComponent
    }

}