package hu.csabapap.seriesreminder.data.db.entities

import android.arch.persistence.room.*

@Entity(tableName = "next_episodes",
        foreignKeys = [(ForeignKey(
                entity = SRShow::class,
                parentColumns = [("trakt_id")],
                childColumns = [("show_id")],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE))],
        indices = [(Index(value = ["show_id"], unique = true))])
class NextEpisodeEntry(
    @PrimaryKey(autoGenerate = true) val _id: Long?,
    @ColumnInfo() val season: String,
    @ColumnInfo() val number: String,
    @ColumnInfo() val title: String,
    @ColumnInfo(name = "trakt_id") val traktId: Int,
    @ColumnInfo(name = "tvdb_id") val tvdbId: Int,
    @ColumnInfo(name = "show_id") val showId: Int
)