package hu.csabapap.seriesreminder.data.db.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import hu.csabapap.seriesreminder.data.db.entities.SRSeason

@Dao
interface SeasonsDao {
    @Insert
    fun insert(season: SRSeason)
}