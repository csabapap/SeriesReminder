package hu.csabapap.seriesreminder.data.db.daos

import android.database.sqlite.SQLiteDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.LastRequest
import hu.csabapap.seriesreminder.data.db.entities.Request
import org.threeten.bp.Instant
import org.threeten.bp.temporal.TemporalAmount

@Dao
abstract class LastRequestDao {

    @Insert(onConflict = SQLiteDatabase.CONFLICT_REPLACE)
    abstract fun insert(request: LastRequest)

    @Query("SELECT * FROM LAST_REQUEST WHERE entity_id = :id AND request = :request")
    abstract fun getLastRequestById(id: Int, request: Request): LastRequest?

    @Query("SELECT * FROM LAST_REQUEST WHERE request = :request LIMIT 1")
    abstract fun getLastRequestByType(request: Request): LastRequest?

    fun isRequestBefore(lastRequest: LastRequest?, threshold: TemporalAmount): Boolean {
        return lastRequest?.timestamp?.isBefore(Instant.now().minus(threshold)) ?: false
    }

    fun isRequestAfter(lastRequest: LastRequest?, threshold: TemporalAmount): Boolean {
        return lastRequest?.timestamp?.isAfter(Instant.now().minus(threshold)) ?: false
    }

}