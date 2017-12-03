package hu.csabapap.seriesreminder.data.network

import android.util.Log
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.intellij.lang.annotations.Flow
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class TraktApi {

    val TAG = "TraktApi"

    init {
        Log.d(TAG, "init block called")
    }

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
            HttpLoggingInterceptor.Logger { Log.d("OkHttp", it) })
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

    fun trendingShows(extended: String = "") : Flowable<List<TrendingShow>>{
        return retrofit.create(ShowsService::class.java).trendingShows(extended)
    }

    fun popularShows() : Flowable<List<Show>>{
        return retrofit.create(ShowsService::class.java).popularShows()
    }

    fun show(traktId: Int) : Flowable<Show>{
        return retrofit.create(ShowsService::class.java).show(traktId)
    }

}