package hu.csabapap.seriesreminder

import com.gabrielittner.threetenbp.LazyThreeTen
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.inject.AppComponent
import hu.csabapap.seriesreminder.inject.DaggerAppComponent
import hu.csabapap.seriesreminder.utils.TvdbRequestHandler
import javax.inject.Inject

open class SRApplication : DaggerApplication() {

    @Inject
    lateinit var tvdbApi: TvdbApi
    lateinit var appComponent : AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        LazyThreeTen.init(this)

        initPicasso()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build()
        return appComponent
    }

    private fun initPicasso() {
        val downloader = OkHttp3Downloader(this)
        val requestHandler = TvdbRequestHandler(tvdbApi, downloader)
        val picasso = Picasso.Builder(this)
                .downloader(downloader)
                .addRequestHandler(requestHandler)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .build()
        Picasso.setSingletonInstance(picasso)
    }
}