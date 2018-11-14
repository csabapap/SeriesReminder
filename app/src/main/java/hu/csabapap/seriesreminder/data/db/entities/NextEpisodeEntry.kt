package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.*

@Entity(tableName = "next_episodes",
        foreignKeys = [(ForeignKey(
                entity = SRShow::class,
                parentColumns = [("trakt_id")],
                childColumns = [("show_id")],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)),
            (ForeignKey(
                    entity = SREpisode::class,
                    parentColumns = [("trakt_id")],
                    childColumns = [("trakt_id")],
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE)),
            (ForeignKey(
                    entity = CollectionEntry::class,
                    parentColumns = ["id"],
                    childColumns = ["collection_id"],
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE
            ))],
        indices = [(Index(value = ["show_id"], unique = true))])
class NextEpisodeEntry(
        @PrimaryKey(autoGenerate = true) val _id: Long?,
        @ColumnInfo() val season: Int,
        @ColumnInfo() val number: Int,
        @ColumnInfo() val title: String,
        @ColumnInfo(name = "trakt_id") val traktId: Int,
        @ColumnInfo(name = "tvdb_id") val tvdbId: Int,
        @ColumnInfo(name = "show_id") val showId: Int,
        @ColumnInfo(name = "collection_id") val collectionId: Int
)

data class NextEpisodeItem(
        @Embedded var entry: NextEpisodeEntry? = null,
        @Relation(parentColumn = "show_id", entityColumn = "trakt_id")
        var shows: List<SRShow>? = null,
        @Relation(parentColumn = "trakt_id", entityColumn = "trakt_id")
        var episodes: List<SREpisode>? = null
) {
    val show: SRShow?
        get() = shows?.getOrNull(0)

    val episode: SREpisode?
        get() = episodes?.getOrNull(0)
}