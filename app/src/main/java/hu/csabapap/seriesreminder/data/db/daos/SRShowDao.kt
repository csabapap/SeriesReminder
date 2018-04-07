package hu.csabapap.seriesreminder.data.db.daos

import android.arch.persistence.room.*
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class SRShowDao {

    @Query("SELECT * FROM shows")
    abstract fun getAllShows() : List<SRShow>

    @Query("SELECT * FROM shows")
    abstract fun getAllShowsSingle(): Single<List<SRShow>>

    @Query("SELECT * FROM shows WHERE trakt_id = :id")
    abstract fun getShowMaybe(id: Int) : Maybe<SRShow>

    @Query("SELECT * FROM shows WHERE trakt_id = :id")
    abstract fun getShow(id: Int) : SRShow?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(show: SRShow) : Long

    @Update
    abstract fun updateShow(show: SRShow)

    fun insertOrUpdateShow(show: SRShow) : SRShow = when {
        show.id == null -> {
            show.copy(id = insert(show))
        } else -> {
            updateShow(show)
            show
        }
    }
}