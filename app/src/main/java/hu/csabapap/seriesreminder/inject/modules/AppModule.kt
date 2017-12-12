package hu.csabapap.seriesreminder.inject.modules

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.data.db.SRDatabase
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
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
}