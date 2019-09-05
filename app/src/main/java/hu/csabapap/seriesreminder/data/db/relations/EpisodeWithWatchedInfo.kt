package hu.csabapap.seriesreminder.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode

data class EpisodeWithWatchedInfo(
    @Embedded val episode: SREpisode,
    @Relation(parentColumn = "trakt_id", entityColumn = "episode_id")
    val watched: List<WatchedEpisode>?
)
