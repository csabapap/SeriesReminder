package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.SRReminder

@Dao
interface RemindersDao {
    @Insert
    fun insert(reminder: SRReminder)

    @Query("SELECT * FROM reminders WHERE show_id = :id")
    fun getReminder(id: Int): SRReminder
}