package hu.csabapap.seriesreminder.data.db.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Update
import hu.csabapap.seriesreminder.data.db.entities.SREpisode

@Dao
abstract class EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(episode: SREpisode)

    @Update
    abstract fun update(episode: SREpisode)

}