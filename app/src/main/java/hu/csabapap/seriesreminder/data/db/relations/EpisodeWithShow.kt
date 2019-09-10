package hu.csabapap.seriesreminder.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode

data class EpisodeWithShow(
        @Embedded val episode: SREpisode,
        @Relation(parentColumn = "show_id", entityColumn = "trakt_id")
        val shows: List<SRShow>?,
        @Relation(parentColumn = "id", entityColumn = "episode_id")
        val watchedEpisode: List<WatchedEpisode>?) {

    val show: SRShow?
        get() = shows?.getOrNull(0)

    fun isWatched(): Boolean {
        return watchedEpisode != null && watchedEpisode.isNotEmpty()
    }
}