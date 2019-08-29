package hu.csabapap.seriesreminder.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason

data class SeasonWithEpisodes(
        @Embedded val season: SRSeason,
        @Relation(parentColumn = "id", entityColumn = "season_id")
        val episodes: List<SREpisode>?)