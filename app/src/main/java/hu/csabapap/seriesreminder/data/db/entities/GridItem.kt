package hu.csabapap.seriesreminder.data.db.entities

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

interface GridItem<T: Item> {
    var entry: T?
    var relations : List<SRShow>?

    val show: SRShow?
        get() = relations?.getOrNull(0)
}

data class TrendingGridItem(
        @Embedded override var entry: SRTrendingItem? = null,
        @Relation(parentColumn = "show_id", entityColumn = "trakt_id")
        override var relations: List<SRShow>? = null
) : GridItem<SRTrendingItem>

data class PopularGridItem(
        @Embedded override var entry: SRPopularItem? = null,
        @Relation(parentColumn = "show_id", entityColumn = "trakt_id")
        override var relations: List<SRShow>? = null
) : GridItem<SRPopularItem>