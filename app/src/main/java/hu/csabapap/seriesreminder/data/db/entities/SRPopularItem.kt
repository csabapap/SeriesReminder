package hu.csabapap.seriesreminder.data.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey


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