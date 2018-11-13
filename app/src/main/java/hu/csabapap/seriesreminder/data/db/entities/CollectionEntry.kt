package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.*
import org.threeten.bp.OffsetDateTime

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
        @ColumnInfo(name = "show_id") override val showId: Int,
        @ColumnInfo(name="added") var added: OffsetDateTime? = null,
        @ColumnInfo(name ="last_watched") var lastWatched: OffsetDateTime? = null
) : Item