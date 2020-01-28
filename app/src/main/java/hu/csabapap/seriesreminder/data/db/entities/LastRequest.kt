package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.Instant

@Entity(tableName = "last_request",
        indices = [Index(value = ["request", "entity_id"], unique = true)])
data class LastRequest(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "entity_id") val traktId: Int,
        @ColumnInfo(name = "request") val request: Request,
        @ColumnInfo(name = "timestamp") val timestamp: Instant
) {
    companion object {
        const val SYNC_SHOWS_ID = -1
    }
}