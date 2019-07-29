package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.*
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

    @Query("UPDATE shows SET next_episode = :nextEpisodeNumber WHERE trakt_id = :showId")
    abstract suspend fun updateNextEpisode(showId: Int, nextEpisodeNumber: Int)

    fun insertOrUpdateShow(show: SRShow) : SRShow = when {
        show.id == null -> {
            show.copy(id = insert(show))
        } else -> {
            updateShow(show)
            show
        }
    }
}