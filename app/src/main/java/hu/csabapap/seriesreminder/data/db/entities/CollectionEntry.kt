package hu.csabapap.seriesreminder.data.db.entities

import android.arch.persistence.room.*

@Entity(tableName = "collection",
        foreignKeys = [(ForeignKey(
                entity = SRShow::class,
                parentColumns = ["trakt_id"],
                childColumns = ["show_id"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE))],
        indices = [(Index(value = ["show_id"], unique = true))]
)
data class CollectionEntry(
        @PrimaryKey(autoGenerate = true) override val id: Long? = null,
        @ColumnInfo(name = "show_id") override val showId: Int
) : Item