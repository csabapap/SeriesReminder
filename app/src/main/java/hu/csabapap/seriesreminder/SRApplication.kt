package hu.csabapap.seriesreminder

import androidx.work.Configuration
import androidx.work.WorkManager
import com.gabrielittner.threetenbp.LazyThreeTen
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.inject.AppComponent
import hu.csabapap.seriesreminder.inject.DaggerAppComponent
import hu.csabapap.seriesreminder.services.workers.SRWorkerFactory
import hu.csabapap.seriesreminder.utils.TvdbRequestHandler
import javax.inject.Inject

open class SRApplication : DaggerApplication() {

    @Inject lateinit var tvdbApi: TvdbApi
    @Inject lateinit var episodesRepository: EpisodesRepository
    lateinit var appComponent : AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        LazyThreeTen.init(this)

        val factory: SRWorkerFactory = appComponent.factory()
        WorkManager.initialize(this, Configuration.Builder()
                .setWorkerFactory(factory).build())


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
        val requestHandler = TvdbRequestHandler(tvdbApi, episodesRepository, downloader)
        val picasso = Picasso.Builder(this)
                .downloader(downloader)
                .addRequestHandler(requestHandler)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .build()
        Picasso.setSingletonInstance(picasso)
    }
}