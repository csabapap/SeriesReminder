package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import io.reactivex.Single

@Dao
interface NextEpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(nextEpisode: NextEpisodeEntry)

    @Query("SELECT * FROM next_episodes")
    fun getNextEpisodes() : Single<List<NextEpisodeItem>>

    @Query("SELECT shows.trakt_id AS showId, shows.title AS showTitle, shows.poster_thumb as poster, " +
            "episodes.season AS season, episodes.number AS number, " +
            "episodes.abs_number AS absNumber, episodes.title AS episodeTitle" +
            " FROM episodes JOIN shows ON episodes.show_id = shows.trakt_id AND " +
            "episodes.abs_number = shows.next_episode " +
            "JOIN watched_episodes ON watched_episodes.show_id = shows.trakt_id " +
            "GROUP BY shows.trakt_id ORDER BY watched_episodes.id DESC LIMIT 3")
    suspend fun getNextEpisodeInWatchList(): List<SRNextEpisode>
}