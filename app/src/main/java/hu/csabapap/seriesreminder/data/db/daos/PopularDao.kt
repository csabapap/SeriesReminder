package hu.csabapap.seriesreminder.data.db.daos

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem
import hu.csabapap.seriesreminder.data.db.entities.SRPopularItem
import io.reactivex.Flowable
import org.intellij.lang.annotations.Language

@Dao
interface PopularDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(popularShow: SRPopularItem)

    @Query("SELECT * FROM popular_shows LIMIT :limit")
    fun getPopularShows(limit: Int) : Flowable<List<PopularGridItem>>

    @Query("SELECT * FROM popular_shows")
    fun getPopularShowsLiveFactory(): DataSource.Factory<Int, PopularGridItem>

    @Query("DELETE FROM popular_shows")
    fun deleteAll()

}