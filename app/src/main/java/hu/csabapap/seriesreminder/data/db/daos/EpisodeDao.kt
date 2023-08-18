package hu.csabapap.seriesreminder.data.db.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.ui.main.home.UpcomingEpisode
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(episode: SREpisode): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(episodes: List<SREpisode>)

    @Query("UPDATE episodes SET image = :url WHERE tvdb_id = :tvdbId")
    abstract fun updateEpisodeWithImage(tvdbId: Int, url: String)

    @Update
    abstract fun update(episode: SREpisode)

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND abs_number = :absNumber LIMIT 1")
    abstract suspend fun getByAbsNumber(showId: Int, absNumber: Int): SREpisode?

    @Query("SELECT * FROM episodes WHERE show_id = :showId")
    abstract suspend fun getAllForShow(showId: Int): List<SREpisode>

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND season = :season AND number = :episode LIMIT 1")
    abstract suspend fun getBySeasonAndEpisodeNumber(showId: Int, season: Int, episode: Int): EpisodeWithShow?

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND season = :seasonNumber")
    abstract suspend fun getEpisodesBySeason(showId: Int, seasonNumber: Int): List<SREpisode>

    @Query("SELECT shows.trakt_id as showId, episodes.trakt_id as episodeId, shows.title as showTitle, " +
            "episodes.title as episodeTitle, shows.airs as airsIn, episodes.image as image, " +
            "shows.cover_thumb as showCover" +
            " FROM episodes, shows WHERE episodes.show_id = shows.trakt_id AND datetime(episodes.first_aired) > datetime('now') AND datetime(episodes.first_aired) < datetime('now', '+7 day') GROUP BY episodes.show_id ORDER BY datetime(episodes.first_aired) LIMIT :limit")
    abstract fun getUpcomingEpisodesFlow(limit: Int) : Flow<List<UpcomingEpisode>>

    @Query("SELECT * FROM episodes LEFT JOIN shows ON episodes.show_id = shows.trakt_id WHERE show_id = :showId AND datetime(episodes.first_aired) > datetime('now') ORDER BY datetime(episodes.first_aired) LIMIT 1")
    abstract suspend fun getUpcomingEpisode(showId: Int): EpisodeWithShow?

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND datetime(episodes.first_aired) > datetime('now') ORDER BY datetime(episodes.first_aired) LIMIT 1")
    abstract suspend fun getNextUpcomingShowId(showId: Int): SREpisode?

    @Transaction
    open fun upsert(episode: SREpisode) {
        try {
            insert(episode)
        } catch (e: SQLiteConstraintException) {
            update(episode)
        }
    }

    @Transaction
    open fun upsert(episodes: List<SREpisode>) {
        episodes.onEach {
            val result = insert(it)
            if (result == -1L) {
                update(it)
            }
        }
    }
}