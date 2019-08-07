package hu.csabapap.seriesreminder.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRShow

data class EpisodeWithShow(
        @Embedded val episode: SREpisode,
        @Relation(parentColumn = "show_id", entityColumn = "trakt_id")
        val shows: List<SRShow>?) {

    val show: SRShow?
        get() = shows?.getOrNull(0)
}