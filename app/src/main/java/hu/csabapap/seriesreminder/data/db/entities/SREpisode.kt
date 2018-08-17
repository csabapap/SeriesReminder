package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.*
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "episodes",
        foreignKeys = [ForeignKey(entity = SRShow::class,
                parentColumns = ["trakt_id"],
                childColumns = ["show_id"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)],
        indices = [(Index(value = ["trakt_id"], unique = true))])
data class SREpisode(@PrimaryKey val _id: Long?,
                     val season: Int,
                     val number: Int,
                     val title: String,
                     @ColumnInfo(name = "trakt_id") val traktId: Int,
                     @ColumnInfo(name = "tvdb_id") val tvdbId: Int,
                     @ColumnInfo(name = "abs_number") val absNumber: Int,
                     val overview: String,
                     @ColumnInfo(name = "first_aired") var firstAired: OffsetDateTime? = null,
                     @ColumnInfo(name = "updated_at") val updatedAt: String,
                     val rating: Float,
                     val votes: Int,
                     val image: String,
                     @ColumnInfo(name = "show_id") val showId: Int)