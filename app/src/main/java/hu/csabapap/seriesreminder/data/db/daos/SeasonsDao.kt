package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import hu.csabapap.seriesreminder.data.db.entities.SRSeason

@Dao
interface SeasonsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(season: SRSeason)
}