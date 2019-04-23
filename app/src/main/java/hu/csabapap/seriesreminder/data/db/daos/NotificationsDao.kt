package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.SrNotification

@Dao
interface NotificationsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: SrNotification)

    @Query("SELECT * FROM notifications WHERE show_id = :showId LIMIT 1")
    fun getNotification(showId: Int): SrNotification?
}