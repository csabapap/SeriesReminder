package hu.csabapap.seriesreminder.inject.modules

import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.data.network.TvdbApi
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideTvdbApiModule() : TvdbApi{
        return TvdbApi()
    }
}