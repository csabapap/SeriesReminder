package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "popular_shows",
        foreignKeys = [(ForeignKey(
                entity = SRShow::class,
                parentColumns = ["trakt_id"],
                childColumns = ["show_id"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE))],
        indices = [(Index(value = ["show_id"], unique = true))]
)
data class SRPopularItem(
    @PrimaryKey(autoGenerate = true) override val id: Long? = null,
    @ColumnInfo(name = "show_id") override val showId: Int
) : Item