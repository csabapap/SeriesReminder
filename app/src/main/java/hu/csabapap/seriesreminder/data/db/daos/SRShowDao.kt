package hu.csabapap.seriesreminder.data.db.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import io.reactivex.Maybe

@Dao
interface SRShowDao {

    @Query("SELECT * FROM shows")
    fun getAllShows() : List<SRShow>

    @Query("SELECT * FROM shows WHERE trakt_id = :id")
    fun getShow(id: Int) : Maybe<SRShow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllShows(shows: List<SRShow>)

}