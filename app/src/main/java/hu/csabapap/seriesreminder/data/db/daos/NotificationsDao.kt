package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.SrNotification

@Dao
interface NotificationsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: SrNotification)

    @Query("SELECT * FROM notifications WHERE show_id = :showId LIMIT 1")
    fun getNotification(showId: Int): SrNotification?

    @Update
    fun update(notification: SrNotification)

    @Delete
    fun delete(notification: SrNotification)
}