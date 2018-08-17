package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import hu.csabapap.seriesreminder.data.db.entities.SREpisode

@Dao
abstract class EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(episode: SREpisode)

    @Update
    abstract fun update(episode: SREpisode)

}