package hu.csabapap.seriesreminder.inject.modules

import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideTraktApiModule(@Named("trakt") retrofit: Retrofit) : TraktApi{
        return TraktApi(retrofit)
    }

    @Singleton
    @Provides
    fun provideTvdbApiModule() : TvdbApi{
        return TvdbApi()
    }
}