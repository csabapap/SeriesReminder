package hu.csabapap.seriesreminder.inject.modules

import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideTraktApiModule() : TraktApi{
        return TraktApi()
    }

    @Singleton
    @Provides
    fun provideTvdbApiModule() : TvdbApi{
        return TvdbApi()
    }
}