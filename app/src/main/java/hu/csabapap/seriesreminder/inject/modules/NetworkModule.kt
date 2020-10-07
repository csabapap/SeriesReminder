package hu.csabapap.seriesreminder.inject.modules

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.uwetrottmann.trakt5.TraktV2
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.ApplicationJsonAdapterFactory
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

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

    @Provides @Named("auth")
    fun providesTraktAuthHeaderInterceptor(loggedInUserRepository: LoggedInUserRepository): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            if (loggedInUserRepository.isLoggedIn()) {
                val newRequest = request.newBuilder()
                        .header("Authorization", "Bearer ${loggedInUserRepository.loggedInUser()?.accessToken ?: ""}")
                        .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(request)
            }
        }
    }

    @Provides
    fun providesOkHttp(traktApiInterceptor: Interceptor, @Named("auth") traktAuthInterceptor: Interceptor): OkHttpClient {
        val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger { Timber.tag("TraktOkHttp").d(it) })
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(traktApiInterceptor)
                .addInterceptor(traktAuthInterceptor)
                .build()
    }

    @Provides
    @Named("trakt")
    fun providesTraktRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.trakt.tv")
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
    }

    @Provides
    fun logInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger { Timber.tag("TraktOkHttp").d(it) })
                .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideTrakt(logInterceptor: HttpLoggingInterceptor): TraktV2 {
        return object : TraktV2(
                BuildConfig.TRAKT_CLIENT_ID,
                BuildConfig.TRAKT_SECRET_ID,
                BuildConfig.TRAKT_REDIRECT_URL) {
            override fun setOkHttpClientDefaults(builder: OkHttpClient.Builder) {
                super.setOkHttpClientDefaults(builder)
                builder.addInterceptor(logInterceptor)
            }
        }
    }

    @Provides
    fun provideTraktShowsService(trakt: TraktV2) = trakt.shows()

    @Provides
    fun provideTraktepisodesService(trakt: TraktV2) = trakt.episodes()

    @Provides
    fun provideTraktSeasonsService(trakt: TraktV2) = trakt.seasons()

    @Provides
    fun providesUserService(trakt: TraktV2) = trakt.users()

    @Provides
    fun provedesSeratchService(trakt: TraktV2) = trakt.search()
}