package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import hu.csabapap.seriesreminder.data.db.entities.SRSeason

@Dao
interface SeasonsDao {
    @Insert
    fun insert(season: SRSeason)
}