package hu.csabapap.seriesreminder.data.db.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface NextEpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(nextEpisode: NextEpisodeEntry)

    @Query("SELECT * FROM next_episodes WHERE show_id = :showId LIMIT 1")
    fun getNextEpisode(showId: Int) : Maybe<NextEpisodeItem>

    @Query("SELECT * FROM next_episodes LIMIT :limit")
    fun getNextEpisodes(limit: Int) : Flowable<List<NextEpisodeItem>>

    @Query("SELECT * FROM next_episodes")
    fun getNextEpisodes() : Single<List<NextEpisodeItem>>
}