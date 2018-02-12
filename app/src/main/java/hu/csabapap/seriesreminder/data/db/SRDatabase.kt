package hu.csabapap.seriesreminder.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import hu.csabapap.seriesreminder.data.db.daos.CollectionsDao
import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.SRPopularItem
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem

@Database(entities = [(SRShow::class),
    (SRTrendingItem::class),
    (SRPopularItem::class),
    (CollectionEntry::class)], version = 2)
abstract class SRDatabase : RoomDatabase(){
    abstract fun showDao() : SRShowDao
    abstract fun trendingDao() : TrendingDao
    abstract fun popularDao() : PopularDao
    abstract fun collectionsDao(): CollectionsDao
}