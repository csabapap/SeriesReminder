package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.*

@Entity(tableName = "seasons",
        foreignKeys = [(ForeignKey(entity = SRShow::class,
                parentColumns = ["trakt_id"],
                childColumns = ["show_id"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE))],
        indices = [(Index(value = ["trakt_id"], unique = true))])
data class SRSeason constructor(
        @PrimaryKey val _id: Long?,
        val number: Int,
        @ColumnInfo(name = "trakt_id") val traktId: Int,
        @ColumnInfo(name = "episode_count") val episodeCount: Int,
        @ColumnInfo(name = "aired_episode_count") val airedEpisodeCount: Int,
        @ColumnInfo(name = "show_id") val showId: Int,
        @ColumnInfo(name = "file_name")val fileName: String? = "",
        val thumbnail: String? = "",
        @ColumnInfo(name = "watched_episodes") val nmbOfWatchedEpisodes: Int = 0) {

    @Ignore
    var episodes: List<SREpisode> = arrayListOf()

    @Ignore
    constructor(
            _id: Long?,
            number: Int,
            traktId: Int,
            episodeCount: Int,
            airedEpisodeCount: Int,
            showId: Int,
            fileName: String? = "",
            thumbnail: String? = "",
            nmbOfWatchedEpisodes: Int = 0,
            episodes: List<SREpisode> = arrayListOf()):
            this(_id, number, traktId, episodeCount, airedEpisodeCount, showId, fileName, thumbnail,
                    nmbOfWatchedEpisodes) {
        this.episodes = episodes;
    }

}