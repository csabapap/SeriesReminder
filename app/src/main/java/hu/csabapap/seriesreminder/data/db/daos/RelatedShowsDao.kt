package hu.csabapap.seriesreminder.data.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.RelatedShow
import hu.csabapap.seriesreminder.data.db.relations.RelatedShowWithShow

@Dao
interface RelatedShowsDao {

    @Insert
    fun insert(relatedShows: RelatedShow)

    @Insert
    fun insert(relatedShows: List<RelatedShow>)

    @Query("SELECT * FROM RELATED_SHOWS WHERE relates_to = :showId LIMIT 10")
    fun relatedLiveEntries(showId: Int): LiveData<List<RelatedShowWithShow>>
}