package hu.csabapap.seriesreminder.data.db.entities

import android.arch.persistence.room.*

data class CollectionItem(
        @Embedded var entry: CollectionEntry? = null,
        @Relation(parentColumn = "show_id", entityColumn = "trakt_id")
        var relations: List<SRShow>? = null
) {
    val show: SRShow?
        get() = relations?.getOrNull(0)
}