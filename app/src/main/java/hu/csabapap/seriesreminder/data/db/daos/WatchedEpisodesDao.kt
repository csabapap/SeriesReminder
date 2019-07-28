package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode

@Dao
interface WatchedEpisodesDao {

    @Insert
    fun insert(watchedEpisode: WatchedEpisode)

}