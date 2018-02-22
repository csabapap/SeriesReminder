package hu.csabapap.seriesreminder.data.network

import com.squareup.moshi.Moshi
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.ApplicationJsonAdapterFactory
import hu.csabapap.seriesreminder.data.network.entities.Episode
import hu.csabapap.seriesreminder.data.network.entities.NextEpisode
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import hu.csabapap.seriesreminder.data.network.services.EpisodesService
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

class TraktApi {

    private val moshi = Moshi.Builder()
            .add(ApplicationJsonAdapterFactory.INSTANCE)
            .build()

    private val traktApiInterceptor : Interceptor = Interceptor { chain ->
        val request = chain.request()
        val newRequest = request.newBuilder()
                .header("Content-type", "application/json")
                .header("trakt-api-key", BuildConfig.TRAKT_CLIENT_ID)
                .header("trakt-api-version", "2")
                .build()
        chain.proceed(newRequest)
    }

    private val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { Timber.d(it) })
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttp: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(traktApiInterceptor)
            .build()

    private val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttp)
            .baseUrl("https://api.trakt.tv")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    fun trendingShows(extended: String = "", limit: Int = 20) : Single<List<TrendingShow>> {
        return retrofit.create(ShowsService::class.java).trendingShows(extended, limit)
    }

    fun popularShows(limit: Int = 20) : Single<List<Show>>{
        return retrofit.create(ShowsService::class.java).popularShows(limit)
    }

    fun show(traktId: Int) : Flowable<Show>{
        return retrofit.create(ShowsService::class.java).show(traktId)
    }

    fun nextEpisode(traktId: Int) : Single<Response<NextEpisode>> {
        return retrofit.create(ShowsService::class.java).nextEpisode(traktId)
    }

    fun episode(showId: Int, seasonNumber: Int, number: Int) : Single<Response<Episode>> {
        return retrofit.create(EpisodesService::class.java).episode(showId, seasonNumber, number)
    }
}