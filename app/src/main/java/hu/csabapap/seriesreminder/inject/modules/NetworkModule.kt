package hu.csabapap.seriesreminder.inject.modules

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.ApplicationJsonAdapterFactory
import hu.csabapap.seriesreminder.data.network.services.*
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
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
    fun trendingShowsService(@Named("trakt") retrofit: Retrofit): TrendingShowsService {
        return retrofit.create(TrendingShowsService::class.java)
    }

    @Provides
    fun popularShowsService(@Named("trakt") retrofit: Retrofit): PopularShowsService {
        return retrofit.create(PopularShowsService::class.java)
    }

    @Provides
    fun relatedShowsService(@Named("trakt") retrofit: Retrofit): RelatedShowsService {
        return retrofit.create(RelatedShowsService::class.java)
    }

    @Provides
    fun episodesService(@Named("trakt") retrofit: Retrofit): EpisodesService {
        return retrofit.create(EpisodesService::class.java)
    }

    @Provides
    fun seasonsService(@Named("trakt") retrofit: Retrofit): SeasonsService {
        return retrofit.create(SeasonsService::class.java)
    }

    @Provides
    fun showService(@Named("trakt") retrofit: Retrofit): ShowsService {
        return retrofit.create(ShowsService::class.java)
    }

    @Provides
    fun searchService(@Named("trakt") retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Provides
    fun authService(@Named("trakt") retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    fun usersService(@Named("trakt") retrofit: Retrofit): UsersService {
        return retrofit.create(UsersService::class.java)
    }
}