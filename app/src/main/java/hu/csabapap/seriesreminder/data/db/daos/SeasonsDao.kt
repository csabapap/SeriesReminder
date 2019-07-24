package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.SRSeason

@Dao
interface SeasonsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(season: SRSeason)

    @Query("SELECT * FROM seasons WHERE show_id = :showId ORDER BY number")
    suspend fun getSeasons(showId: Int): List<SRSeason>?
}