package hu.csabapap.seriesreminder.data.db.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow

@Dao
abstract class EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(episode: SREpisode)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(episodes: List<SREpisode>)

    @Query("UPDATE episodes SET image = :url WHERE tvdb_id = :tvdbId")
    abstract fun updateEpisodeWithImage(tvdbId: Int, url: String)

    @Update
    abstract fun update(episode: SREpisode)

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND abs_number = :absNumber LIMIT 1")
    abstract suspend fun getByAbsNumber(showId: Int, absNumber: Int): SREpisode?

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND season = :season AND number = :episode LIMIT 1")
    abstract suspend fun getBySeasonAndEpisodeNumber(showId: Int, season: Int, episode: Int): EpisodeWithShow?

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND season = :seasonNumber")
    abstract suspend fun getEpisodesBySeason(showId: Int, seasonNumber: Int): List<SREpisode>

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
            try {
                insert(it)
            } catch (e: SQLiteConstraintException) {
                update(it)
            }
        }
    }
}