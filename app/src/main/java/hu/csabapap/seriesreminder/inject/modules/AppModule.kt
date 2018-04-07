package hu.csabapap.seriesreminder.inject.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.data.EpisodesRepository
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.daos.*
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import javax.inject.Singleton


@Module
class AppModule{

    @Singleton
    @Provides
    fun provideContext(application: SRApplication): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideRxSchedulers() : AppRxSchedulers {
        return AppRxSchedulers()
    }

    @Singleton
    @Provides
    fun provideShowsRepository(traktApi: TraktApi,
                               tvdbApi: TvdbApi,
                               showDao: SRShowDao,
                               trendingDao: TrendingDao,
                               popularDao: PopularDao,
                               seasonsRepository: SeasonsRepository,
                               episodesRepository: EpisodesRepository,
                               collectionRepository: CollectionRepository)
            : ShowsRepository {
        return ShowsRepository(traktApi, tvdbApi, showDao, trendingDao, popularDao,
                seasonsRepository, episodesRepository, collectionRepository)
    }

    @Singleton
    fun providesCollectionRepository(collectionsDao: CollectionsDao) : CollectionRepository {
        return CollectionRepository(collectionsDao)
    }
}