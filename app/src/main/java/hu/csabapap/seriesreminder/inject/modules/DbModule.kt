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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_4_5, MIGRATION_5_6,
                        MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11,
                        MIGRATION_11_12)
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

    @Provides
    fun providesRelatedShowsDao(db: SRDatabase): RelatedShowsDao {
        return db.relatedShowsDao()
    }

    @Provides
    fun providesWatchedEpisodesDao(db: SRDatabase): WatchedEpisodesDao {
        return db.watchedEpisodesDao()
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

        val MIGRATION_6_7 = object: Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `related_shows` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `related_id` INTEGER NOT NULL, `relates_to` INTEGER NOT NULL, FOREIGN KEY(`relates_to`) REFERENCES `shows`(`trakt_id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`related_id`) REFERENCES `shows`(`trakt_id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
            }
        }

        val MIGRATION_7_8 = object: Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `shows_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `trakt_id` INTEGER UNIQUE NOT NULL, `tvdb_id` INTEGER UNIQUE NOT NULL, `title` TEXT NOT NULL, `overview` TEXT NOT NULL, `poster` TEXT NOT NULL, `poster_thumb` TEXT NOT NULL, `cover` TEXT NOT NULL, `cover_thumb` TEXT NOT NULL, `rating` REAL NOT NULL, `votes` INTEGER NOT NULL, `genres` TEXT NOT NULL, `runtime` INTEGER NOT NULL, `aired_episodes` INTEGER NOT NULL, `status` TEXT NOT NULL, `network` TEXT NOT NULL, `trailer` TEXT NOT NULL, `homepage` TEXT NOT NULL, `updated_at` TEXT, `airs` TEXT NOT NULL, `next_episode` INTEGER NOT NULL DEFAULT -1)")

                database.execSQL("INSERT INTO shows_new(id, trakt_id, tvdb_id, title,overview,poster,poster_thumb,cover,cover_thumb,rating,votes,genres,runtime,aired_episodes,status,network,trailer,homepage,updated_at,airs) SELECT id, trakt_id, tvdb_id, title,overview,poster,poster_thumb,cover,cover_thumb,rating,votes,genres,runtime,aired_episodes,status,network,trailer,homepage,updated_at,airs FROM shows")

                database.execSQL("DROP TABLE shows")

                database.execSQL("ALTER TABLE shows_new RENAME TO shows")

                database.execSQL("CREATE UNIQUE INDEX index_shows_trakt_id ON shows(trakt_id)")
                database.execSQL("CREATE UNIQUE INDEX index_shows_tvdb_id ON shows(tvdb_id)")
            }
        }

        val MIGRATION_8_9 = object: Migration(8,9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `seasons` ADD COLUMN fileName TEXT")
                database.execSQL("ALTER TABLE `seasons` ADD COLUMN thumbnail TEXT")
            }
        }

        val MIGRATION_9_10 = object: Migration(9,10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `seasons_new` (`_id` INTEGER, `number` INTEGER NOT NULL, `trakt_id` INTEGER NOT NULL, `episode_count` INTEGER NOT NULL, `aired_episode_count` INTEGER NOT NULL, `show_id` INTEGER NOT NULL, `file_name` TEXT, `thumbnail` TEXT, `watched_episodes` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`_id`), FOREIGN KEY(`show_id`) REFERENCES `shows`(`trakt_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
                database.execSQL("INSERT INTO seasons_new(_id, number, trakt_id, episode_count, aired_episode_count, show_id, file_name, thumbnail) SELECT _id, number, trakt_id, episode_count, aired_episode_count, show_id, fileName, thumbnail FROM seasons")
                database.execSQL("DROP TABLE seasons")
                database.execSQL("ALTER TABLE seasons_new RENAME TO seasons")
                database.execSQL("CREATE UNIQUE INDEX index_seasons_trakt_id ON seasons(trakt_id)")
            }
        }

        val MIGRATION_10_11 = object: Migration(10,11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `watched_episodes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `show_id` INTEGER NOT NULL, `season` INTEGER NOT NULL, `number` INTEGER NOT NULL, FOREIGN KEY(`show_id`) REFERENCES `shows`(`trakt_id`) ON UPDATE CASCADE ON DELETE CASCADE)")
                database.execSQL("CREATE UNIQUE INDEX `index_watched_episodes_show_id_season_number` ON `watched_episodes` (`show_id`, `season`, `number`)")
            }
        }

        val MIGRATION_11_12 = object: Migration(11,12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `seasons_new` (`id` INTEGER, `number` INTEGER NOT NULL, `trakt_id` INTEGER NOT NULL, `episode_count` INTEGER NOT NULL, `aired_episode_count` INTEGER NOT NULL, `show_id` INTEGER NOT NULL, `file_name` TEXT, `thumbnail` TEXT, `watched_episodes` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`), FOREIGN KEY(`show_id`) REFERENCES `shows`(`trakt_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
                database.execSQL("INSERT INTO seasons_new(id, number, trakt_id, episode_count, aired_episode_count, show_id, file_name, thumbnail) SELECT _id, number, trakt_id, episode_count, aired_episode_count, show_id, file_name, thumbnail FROM seasons")
                database.execSQL("DROP TABLE seasons")
                database.execSQL("ALTER TABLE seasons_new RENAME TO seasons")
                database.execSQL("CREATE UNIQUE INDEX index_seasons_trakt_id ON seasons(trakt_id)")

                database.execSQL("CREATE TABLE IF NOT EXISTS `episodes_new` (`_id` INTEGER, `season` INTEGER NOT NULL, `number` INTEGER NOT NULL, `title` TEXT NOT NULL, `trakt_id` INTEGER NOT NULL, `tvdb_id` INTEGER NOT NULL, `abs_number` INTEGER NOT NULL, `overview` TEXT NOT NULL, `first_aired` TEXT, `updated_at` TEXT NOT NULL, `rating` REAL NOT NULL, `votes` INTEGER NOT NULL, `image` TEXT NOT NULL, `show_id` INTEGER NOT NULL, `season_id` INTEGER NOT NULL, PRIMARY KEY(`_id`), FOREIGN KEY(`show_id`) REFERENCES `shows`(`trakt_id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`season_id`) REFERENCES `seasons`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
                database.execSQL("INSERT INTO episodes_new(_id, season, number, title, trakt_id, tvdb_id, abs_number, overview, first_aired, updated_at,rating,votes,image,show_id,season_id) SELECT _id, season, number, title, trakt_id, tvdb_id, abs_number, overview, first_aired, updated_at,rating,votes,image,show_id,-1 FROM episodes")
                database.execSQL("UPDATE episodes_new  SET season_id = (SELECT id FROM seasons WHERE seasons.show_id = episodes_new.show_id AND seasons.number = episodes_new.season)")
                database.execSQL("DROP TABLE episodes")
                database.execSQL("ALTER TABLE episodes_new RENAME TO episodes")
                database.execSQL("CREATE UNIQUE INDEX `index_episodes_trakt_id` ON `episodes` (`trakt_id`)")
            }
        }
    }
}