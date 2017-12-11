package hu.csabapap.seriesreminder.data.network

import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

class TraktApi {

    val traktApiInterceptor : Interceptor = Interceptor { chain ->
        val request = chain.request()
        val newRequest = request.newBuilder()
                .header("Content-type", "application/json")
                .header("trakt-api-key", BuildConfig.TRAKT_CLIENT_ID)
                .header("trakt-api-version", "2")
                .build()
        chain.proceed(newRequest)
    }

    val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { Timber.d(it) })
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    val okHttp: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(traktApiInterceptor)
            .build()

    val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttp)
            .baseUrl("https://api.trakt.tv")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    fun trendingShows(extended: String = "") : Single<List<TrendingShow>> {
        return retrofit.create(ShowsService::class.java).trendingShows(extended)
    }

    fun popularShows() : Flowable<List<Show>>{
        return retrofit.create(ShowsService::class.java).popularShows()
    }

    fun show(traktId: Int) : Flowable<Show>{
        return retrofit.create(ShowsService::class.java).show(traktId)
    }

}