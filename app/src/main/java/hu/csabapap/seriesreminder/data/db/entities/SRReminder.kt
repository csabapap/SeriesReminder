package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "reminders",
        foreignKeys = [ForeignKey(entity = CollectionEntry::class,
                parentColumns = ["show_id"],
                childColumns = ["show_id"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)])
data class SRReminder(
        @PrimaryKey(autoGenerate = true) val _id: Long?,
        @ColumnInfo(name = "show_id") val showId: Int,
        val delay: Int)