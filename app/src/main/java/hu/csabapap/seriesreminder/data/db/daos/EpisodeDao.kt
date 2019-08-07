package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow

@Dao
abstract class EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(episode: SREpisode)

    @Query("UPDATE episodes SET image = :url WHERE tvdb_id = :tvdbId")
    abstract fun updateEpisodeWithImage(tvdbId: Int, url: String)

    @Update
    abstract fun update(episode: SREpisode)

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND abs_number = :absNumber LIMIT 1")
    abstract suspend fun getByAbsNumber(showId: Int, absNumber: Int): SREpisode?

    @Query("SELECT * FROM episodes WHERE show_id = :showId AND season = :season AND number = :episode LIMIT 1")
    abstract suspend fun getBySeasonAndEpisodeNumber(showId: Int, season: Int, episode: Int): EpisodeWithShow?
}