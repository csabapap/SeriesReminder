package hu.csabapap.seriesreminder.data.db.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.relations.SeasonWithEpisodes
import timber.log.Timber

@Dao
abstract class SeasonsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(season: SRSeason): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertCoroutine(season: SRSeason): Long

    @Query("SELECT * FROM seasons WHERE show_id = :showId ORDER BY number")
    abstract suspend fun getSeasons(showId: Int): List<SRSeason>?

    @Query("SELECT * FROM seasons WHERE show_id = :showId AND number > 0 ORDER BY number")
    abstract fun getSeasonsLiveData(showId: Int): LiveData<List<SRSeason>>

    @Query("SELECT * FROM seasons WHERE show_id = :showId AND number = :season LIMIT 1")
    abstract suspend fun getSeason(showId: Int, season: Int): SRSeason?

    @Query("SELECT * FROM seasons WHERE show_id = :showId AND number = :season LIMIT 1")
    abstract suspend fun getSeasonWithEpisodes(showId: Int, season: Int): SeasonWithEpisodes?

    @Update
    abstract suspend fun update(season: SRSeason)

    @Update
    abstract fun updateSync(season: SRSeason)

    @Transaction
    open fun upsert(season: SRSeason) {
        try {
            insert(season)
        } catch (e: SQLiteConstraintException) {
            updateSync(season)
        }
    }

    @Transaction
    open suspend fun upsert(seasons: List<SRSeason>) {
        seasons.onEach {
            try {
                insertCoroutine(it)
            } catch (e: SQLiteConstraintException) {
                update(it)
            }
        }
    }
}