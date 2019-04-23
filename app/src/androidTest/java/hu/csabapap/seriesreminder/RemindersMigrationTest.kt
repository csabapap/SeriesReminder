package hu.csabapap.seriesreminder

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.InstrumentationRegistry
import hu.csabapap.seriesreminder.data.db.SRDatabase
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.SRReminder
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.inject.modules.DbModule
import hu.csabapap.seriesreminder.inject.modules.DbModule.Companion.MIGRATION_1_2
import hu.csabapap.seriesreminder.inject.modules.DbModule.Companion.MIGRATION_2_3
import hu.csabapap.seriesreminder.inject.modules.DbModule.Companion.MIGRATION_3_4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.OffsetDateTime
import java.io.IOException


class RemindersMigrationTest {
    companion object {
        const val DB_NAME = "test.db"

        val SHOW = SRShow(null, 1, 1, "Modern Family", "Lorem ipsum")
        val COLLECTION_ENTRY = CollectionEntry(null, 1, OffsetDateTime.now())
        val REMINDER = SRReminder(null, 1, -2)
    }

    @get:Rule
    var migrationTestHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            SRDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())


    @Test
    @Throws(IOException::class)
    fun migration_from_3_to_4_has_reminders_table() {

        migrationTestHelper.createDatabase(DB_NAME, 3)

        migrationTestHelper.runMigrationsAndValidate(DB_NAME, 4, false,
                DbModule.MIGRATION_3_4)

        val showsDao = getMigratedRoomDatabase().showDao()
        val collectionsDao = getMigratedRoomDatabase().collectionsDao()
//        val remindersDao = getMigratedRoomDatabase().remindersDao()

        showsDao.insert(SHOW)
        collectionsDao.insert(COLLECTION_ENTRY)
//        remindersDao.insert(REMINDER)
//
//        val reminder = remindersDao.getReminder(REMINDER.showId)

//        assertEquals(REMINDER.delay, reminder.delay)
    }

    private fun getMigratedRoomDatabase(): SRDatabase {
        val database = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(),
                SRDatabase::class.java, DB_NAME)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
        // close the database and release any stream resources when the test finishes
        migrationTestHelper.closeWhenFinished(database)
        return database
    }
}