package hu.csabapap.seriesreminder.data.db.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import io.reactivex.Flowable
import org.intellij.lang.annotations.Language

@Dao
interface TrendingDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(trendingItem: SRTrendingItem)

    @Language("RoomSql")
    @Query("SELECT * FROM trending_shows ORDER BY watchers DESC LIMIT :limit")
    fun getTrendingShows(limit: Int) : Flowable<List<TrendingGridItem>>

}