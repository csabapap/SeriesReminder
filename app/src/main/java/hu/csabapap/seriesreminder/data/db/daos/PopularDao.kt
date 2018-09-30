package hu.csabapap.seriesreminder.data.db.daos

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem
import hu.csabapap.seriesreminder.data.db.entities.SRPopularItem
import io.reactivex.Flowable
import org.intellij.lang.annotations.Language

@Dao
interface PopularDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(popularShow: SRPopularItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(popularShows: List<SRPopularItem>)

    @Query("SELECT * FROM popular_shows LIMIT :limit")
    fun getPopularShows(limit: Int) : Flowable<List<PopularGridItem>>

    @Query("SELECT * FROM popular_shows ORDER BY page ASC LIMIT :limit")
    fun getPopularShowsLiveFactory(limit: Int): DataSource.Factory<Int, PopularGridItem>

    @Query("DELETE FROM popular_shows WHERE page = :page")
    fun delete(page: Int)

    @Query("DELETE FROM popular_shows")
    fun deleteAll()

}