package hu.csabapap.seriesreminder.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.entities.SRShow

data class ShowWithSeaons(
    @Embedded
    val show: SRShow,
    @Relation(parentColumn = "trakt_id", entityColumn = "show_id")
    val seasons: List<SRSeason>?)