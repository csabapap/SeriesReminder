package hu.csabapap.seriesreminder.data.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.relations.SeasonWithEpisodes
import kotlinx.coroutines.coroutineScope

@Dao
abstract class SeasonsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(season: SRSeason): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertCoroutine(season: SRSeason): Long

    @Query("SELECT * FROM seasons WHERE show_id = :showId ORDER BY number")
    abstract suspend fun getSeasons(showId: Int): List<SRSeason>?

    @Query("SELECT * FROM seasons WHERE show_id = :showId AND number > 0 ORDER BY number")
    abstract fun getSeasonsLiveData(showId: Int): LiveData<List<SRSeason>>

    @Query("SELECT * FROM seasons WHERE show_id = :showId AND number = :season LIMIT 1")
    abstract suspend fun getSeason(showId: Int, season: Int): SRSeason?

    @Query("SELECT * FROM seasons WHERE show_id = :showId AND number = :season LIMIT 1")
    abstract suspend fun getSeasonWithEpisodes(showId: Int, season: Int): SeasonWithEpisodes?

    @Update(onConflict = REPLACE)
    abstract suspend fun update(season: SRSeason)

    @Update
    abstract suspend fun update(season: List<SRSeason>)

    @Update
    abstract fun updateSync(season: SRSeason)

    @Transaction
    open suspend fun upsert(seasons: List<SRSeason>) = coroutineScope {
        seasons.map {
            val result = insertCoroutine(it)
            if (result == -1L) {
                update(it)
            }
        }
    }
}