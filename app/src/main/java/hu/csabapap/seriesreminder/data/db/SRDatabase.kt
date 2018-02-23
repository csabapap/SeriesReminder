package hu.csabapap.seriesreminder.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import hu.csabapap.seriesreminder.data.db.daos.*
import hu.csabapap.seriesreminder.data.db.entities.*

@Database(entities = [(SRShow::class),
    (SRTrendingItem::class),
    (SRPopularItem::class),
    (CollectionEntry::class),
    (NextEpisodeEntry::class),
    (SREpisode::class)], version = 2)
@TypeConverters(AiringTime::class, SRTypeConverters::class)
abstract class SRDatabase : RoomDatabase(){
    abstract fun showDao() : SRShowDao
    abstract fun trendingDao() : TrendingDao
    abstract fun popularDao() : PopularDao
    abstract fun collectionsDao(): CollectionsDao
    abstract fun nextEpisodesDao(): NextEpisodeDao
    abstract fun episodesDao(): EpisodeDao
}