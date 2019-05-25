package hu.csabapap.seriesreminder.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import hu.csabapap.seriesreminder.data.db.entities.RelatedShow
import hu.csabapap.seriesreminder.data.db.entities.SRShow

data class RelatedShowWithShow(
        @Embedded var entry: RelatedShow? = null,
        @Relation(parentColumn = "related_id", entityColumn = "trakt_id")
        var shows: List<SRShow>? = null
        ) {
    val show: SRShow?
        get() = shows?.getOrNull(0)
}