package hu.csabapap.seriesreminder.data.db.daos

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import io.reactivex.Flowable
import org.intellij.lang.annotations.Language

@Dao
interface TrendingDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(trendingItem: SRTrendingItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trendingItems: List<SRTrendingItem>)

    @Language("RoomSql")
    @Query("SELECT * FROM trending_shows ORDER BY page ASC, watchers DESC LIMIT :limit")
    fun getTrendingShowsFlowable(limit: Int) : Flowable<List<TrendingGridItem>>

    @Query("SELECT * FROM trending_shows ORDER BY page ASC, watchers DESC LIMIT :limit")
    suspend fun getTrendingShows(limit: Int) : List<TrendingGridItem>?

    @Query("SELECT * FROM trending_shows ORDER BY page ASC, watchers DESC LIMIT :limit")
    fun getTrendingShowsFactory(limit: Int): DataSource.Factory<Int, TrendingGridItem>

    @Query("SELECT * FROM trending_shows ORDER BY watchers DESC LIMIT :limit")
    fun getLiveTrendingShows(limit: Int) : LiveData<List<TrendingGridItem>>

    @Query("SELECT MAX(page) FROM trending_shows;")
    fun getLastPage(): Int?

    @Query("DELETE FROM trending_shows")
    fun deleteAll()

    @Query("DELETE FROM trending_shows WHERE page = :page")
    fun delete(page:Int)

}