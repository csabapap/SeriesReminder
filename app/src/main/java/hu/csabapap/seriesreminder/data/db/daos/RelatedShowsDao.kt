package hu.csabapap.seriesreminder.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.RelatedShow

@Dao
interface RelatedShowsDao {

    @Insert
    fun insert(relatedShows: RelatedShow)

    @Insert
    fun insert(relatedShows: List<RelatedShow>)
}