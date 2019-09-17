package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithWatchedInfo

@Dao
interface WatchedEpisodesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(watchedEpisode: WatchedEpisode): Long

    @Query("SELECT * FROM watched_episodes WHERE show_id = :showId AND season = :season AND number = :number LIMIT 1")
    suspend fun get(showId: Int, season: Int, number: Int): WatchedEpisode

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND season = :seasonNumber")
    suspend fun getEpisodesWithWatchedInfo(showId: Int, seasonNumber: Int): List<EpisodeWithWatchedInfo>?

    @Delete
    suspend fun delete(watchedEpisode: WatchedEpisode): Int
}