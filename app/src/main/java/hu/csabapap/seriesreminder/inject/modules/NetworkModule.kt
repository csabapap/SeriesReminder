package hu.csabapap.seriesreminder.inject.modules

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.ApplicationJsonAdapterFactory
import hu.csabapap.seriesreminder.data.network.services.RelatedShowsService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Named

@Module
class NetworkModule {

    @Provides
    fun providesMoshi(): Moshi {
        return Moshi.Builder()
                .add(ApplicationJsonAdapterFactory.INSTANCE)
                .build()
    }

    @Provides
    fun providesTraktHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                    .header("Content-type", "application/json")
                    .header("trakt-api-key", BuildConfig.TRAKT_CLIENT_ID)
                    .header("trakt-api-version", "2")
                    .build()
            chain.proceed(newRequest)
        }
    }

    @Provides
    fun providesOkHttp(traktApiInterceptor: Interceptor): OkHttpClient {
        val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger { Timber.tag("TraktOkHttp").d(it) })
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(traktApiInterceptor)
                .build()
    }

    @Provides
    @Named("trakt")
    fun providesTraktRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.trakt.tv")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
    }

    @Provides
    fun relatedShowsService(@Named("trakt") retrofit: Retrofit): RelatedShowsService {
        return retrofit.create(RelatedShowsService::class.java)
    }
}