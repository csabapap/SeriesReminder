package hu.csabapap.seriesreminder.inject.modules

import androidx.room.Room
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_4_5, MIGRATION_5_6)
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

    @Singleton
    @Provides
    fun providesLastRequestDao(db: SRDatabase) : LastRequestDao {
        return db.lastRequestDao()
    }

    @Provides
    fun providesNotificationsDao(db: SRDatabase): NotificationsDao {
        return db.notificationsDao()
    }

    companion object {
        val MIGRATION_1_2 = object : Migration(1,2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE trending_shows ADD COLUMN page INTEGER DEFAULT 0")
                database.execSQL("ALTER TABLE popular_shows ADD COLUMN page INTEGER DEFAULT 0")
            }

        }

        val MIGRATION_2_3 = object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE collection ADD COLUMN added TEXT")
                database.execSQL("ALTER TABLE collection ADD COLUMN last_watched TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3,4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `reminders` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, `show_id` INTEGER NOT NULL, `delay` INTEGER NOT NULL, FOREIGN KEY(`show_id`) REFERENCES `collection`(`show_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
            }

        }

        val MIGRATION_4_5 = object : Migration(3,4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, `show_id` INTEGER NOT NULL, `delay` INTEGER NOT NULL, FOREIGN KEY(`show_id`) REFERENCES `shows`(`trakt_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
                database.execSQL("DROP TABLE IF EXISTS `reminders`")
            }
        }

        val MIGRATION_5_6 = object: Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `notifications` ADD COLUMN worker_id TEXT")
            }
        }
    }
}