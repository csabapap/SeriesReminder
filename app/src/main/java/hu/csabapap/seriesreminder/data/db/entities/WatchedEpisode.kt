package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.*
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "watched_episodes",
        foreignKeys = [ForeignKey(
                entity = SRShow::class,
                parentColumns = ["trakt_id"],
                childColumns = ["show_id"]
        , onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
            ForeignKey(
                    entity = SREpisode::class,
                    parentColumns = ["id"],
                    childColumns = ["episode_id"]
                    , onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)],
        indices = [Index(value = ["show_id", "season", "number"],
                unique = true)])
data class WatchedEpisode(
        @PrimaryKey(autoGenerate = true) val id: Long? = null,
        @ColumnInfo(name = "show_id") val showId: Int,
        @ColumnInfo(name = "season") val season: Int,
        @ColumnInfo(name = "number") val number: Int,
        @ColumnInfo(name = "episode_id") val episodeId: Long,
        @ColumnInfo(name = "watched_at") val watchedAt: OffsetDateTime
)