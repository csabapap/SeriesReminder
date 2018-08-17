package hu.csabapap.seriesreminder.inject.modules

import androidx.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.data.db.SRDatabase
import hu.csabapap.seriesreminder.data.db.daos.*
import javax.inject.Singleton

@Module
class DbModule {

    @Singleton
    @Provides
    fun providesDatabase(context: Context) : SRDatabase {
        return Room.databaseBuilder(context, SRDatabase::class.java, "series_reminder.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun providesShowsDao(db: SRDatabase): SRShowDao {
        return db.showDao()
    }

    @Singleton
    @Provides
    fun providesTrendingDao(db: SRDatabase) : TrendingDao {
        return db.trendingDao()
    }

    @Singleton
    @Provides
    fun providesPopularDao(db: SRDatabase) : PopularDao {
        return db.popularDao()
    }

    @Singleton
    @Provides
    fun providesCollectionsDao(db: SRDatabase) : CollectionsDao {
        return db.collectionsDao()
    }

    @Singleton
    @Provides
    fun providesNextEpisodesDao(db: SRDatabase) : NextEpisodeDao {
        return db.nextEpisodesDao()
    }

    @Singleton
    @Provides
    fun providesSeasonsDao(db: SRDatabase) : SeasonsDao {
        return db.seasonsDao()
    }

    @Singleton
    @Provides
    fun providesEpisodesDao(db: SRDatabase) : EpisodeDao {
        return db.episodesDao()
    }
}