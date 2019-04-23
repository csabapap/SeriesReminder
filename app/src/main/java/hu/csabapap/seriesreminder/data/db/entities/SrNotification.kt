package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "notifications",
        foreignKeys = [ForeignKey(entity = SRShow::class,
                parentColumns = ["trakt_id"],
                childColumns = ["show_id"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)])
data class SrNotification(
        @PrimaryKey(autoGenerate = true) val _id: Long?,
        @ColumnInfo(name = "show_id") val showId: Int,
        val delay: Int
)