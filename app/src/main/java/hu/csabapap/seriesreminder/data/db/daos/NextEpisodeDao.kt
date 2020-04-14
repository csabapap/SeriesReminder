package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface NextEpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(nextEpisode: NextEpisodeEntry)

    @Query("SELECT * FROM next_episodes WHERE show_id = :showId LIMIT 1")
    fun getNextEpisode(showId: Int) : Maybe<NextEpisodeItem>

    @Query("SELECT * FROM next_episodes LEFT JOIN episodes ON next_episodes.trakt_id = episodes.trakt_id WHERE datetime(episodes.first_aired) > datetime('now') AND datetime(episodes.first_aired) < datetime('now', '+7 day') ORDER BY datetime(episodes.first_aired) LIMIT :limit")
    fun getNextEpisodes(limit: Int) : Flowable<List<NextEpisodeItem>>

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