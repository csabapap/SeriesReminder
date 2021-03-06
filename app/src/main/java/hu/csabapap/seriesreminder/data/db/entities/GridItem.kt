package hu.csabapap.seriesreminder.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

interface GridItem<T: Item> {
    var entry: T?
    var relations : List<SRShow>?
    var collectionEntry: List<CollectionEntry>?

    val show: SRShow?
        get() = relations?.getOrNull(0)

    val inCollection: Boolean
        get() = collectionEntry?.getOrNull(0) != null
}

data class TrendingGridItem(
        @Embedded override var entry: SRTrendingItem? = null,
        @Relation(parentColumn = "show_id", entityColumn = "trakt_id")
        override var relations: List<SRShow>? = null,
        @Relation(parentColumn = "show_id", entityColumn = "show_id")
        override var collectionEntry: List<CollectionEntry>? = null
) : GridItem<SRTrendingItem>

data class PopularGridItem(
        @Embedded override var entry: SRPopularItem? = null,
        @Relation(parentColumn = "show_id", entityColumn = "trakt_id")
        override var relations: List<SRShow>? = null,
        @Relation(parentColumn = "show_id", entityColumn = "show_id")
        override var collectionEntry: List<CollectionEntry>? = null
) : GridItem<SRPopularItem>

data class MyShowGridItem(
        @Embedded var entry: CollectionEntry? = null,
        @Relation(parentColumn = "show_id", entityColumn = "trakt_id")
        var relations: List<SRShow>? = null) {
    val show: SRShow?
        get() = relations?.getOrNull(0)
}