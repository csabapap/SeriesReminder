package hu.csabapap.seriesreminder.inject.modules

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.data.db.SRDatabase
import hu.csabapap.seriesreminder.data.db.daos.CollectionsDao
import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
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

}