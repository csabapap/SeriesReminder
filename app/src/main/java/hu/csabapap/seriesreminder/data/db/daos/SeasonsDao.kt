package hu.csabapap.seriesreminder.data.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.SRSeason

@Dao
interface SeasonsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(season: SRSeason)

    @Query("SELECT * FROM seasons WHERE show_id = :showId ORDER BY number")
    suspend fun getSeasons(showId: Int): List<SRSeason>?

    @Query("SELECT * FROM seasons WHERE show_id = :showId AND number > 0 ORDER BY number")
    fun getSeasonsLiveData(showId: Int): LiveData<List<SRSeason>>

    @Query("SELECT * FROM seasons WHERE show_id = :showId AND number = :season LIMIT 1")
    suspend fun getSeason(showId: Int, season: Int): SRSeason?

    @Update
    suspend fun update(season: SRSeason)
}