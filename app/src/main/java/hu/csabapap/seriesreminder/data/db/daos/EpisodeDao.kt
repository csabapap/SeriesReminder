package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.SREpisode

@Dao
abstract class EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(episode: SREpisode)

    @Update
    abstract fun update(episode: SREpisode)

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND abs_number = :absNumber LIMIT 1")
    abstract fun get(showId: Int, absNumber: Int): SREpisode?
}