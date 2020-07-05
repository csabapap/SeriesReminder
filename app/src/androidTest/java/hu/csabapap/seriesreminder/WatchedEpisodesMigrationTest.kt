package hu.csabapap.seriesreminder

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.InstrumentationRegistry
import hu.csabapap.seriesreminder.data.db.SRDatabase
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.inject.modules.DbModule
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.io.IOException

class WatchedEpisodesMigrationTest {

    companion object {
        const val DB_NAME = "test.db"

        val SHOW = SRShow(null, 1, 1, "Modern Family", "Lorem ipsum")
        val EPISODE = SREpisode(null, 1, 1, "Pilot", 1, 1,
                1, "Lorem ipsum", OffsetDateTime.now(),
                OffsetDateTime.now().toString(), 5.0f,123, "",1)
        val WATCHED_EPISODE = WatchedEpisode(null,1, 1, 1, -1,
                OffsetDateTime.of(2020, 3, 3, 12, 0, 0, 0, ZoneOffset.UTC))
    }

    @get:Rule
    var migrationTestHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            SRDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Test
    @Throws(IOException::class)
    fun migration_from_10_to_11_add_watched_episodes_table() = runBlocking {

        migrationTestHelper.createDatabase(RemindersMigrationTest.DB_NAME, 10)

        migrationTestHelper.runMigrationsAndValidate(RemindersMigrationTest.DB_NAME, 11, false,
                DbModule.MIGRATION_10_11)

        val showsDao = getMigratedRoomDatabase().showDao()
        val episodesDao = getMigratedRoomDatabase().episodesDao()
        val watchedEpisodesDao =  getMigratedRoomDatabase().watchedEpisodesDao()

        showsDao.insert(SHOW)
        episodesDao.insert(EPISODE)
        watchedEpisodesDao.insert(WATCHED_EPISODE)

        val watchedEpisode = watchedEpisodesDao.get(SHOW.traktId, EPISODE.season, EPISODE.number)

        assertEquals(EPISODE.season, watchedEpisode.season)
        assertEquals(EPISODE.number, watchedEpisode.number)
        assertEquals(EPISODE.showId, watchedEpisode.showId)
    }

    private fun getMigratedRoomDatabase(): SRDatabase {
        val database = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(),
                SRDatabase::class.java, DB_NAME)
                .addMigrations(DbModule.MIGRATION_1_2, DbModule.MIGRATION_2_3,DbModule.MIGRATION_4_5,
                        DbModule.MIGRATION_5_6, DbModule.MIGRATION_6_7, DbModule.MIGRATION_7_8,
                        DbModule.MIGRATION_8_9, DbModule.MIGRATION_9_10, DbModule.MIGRATION_10_11)
                .build()
        // close the database and release any stream resources when the test finishes
        migrationTestHelper.closeWhenFinished(database)
        return database
    }

}