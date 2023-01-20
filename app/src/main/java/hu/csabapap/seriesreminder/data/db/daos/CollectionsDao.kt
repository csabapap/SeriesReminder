package hu.csabapap.seriesreminder.data.db.daos

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.db.entities.MyShowGridItem

@Dao
interface CollectionsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(collectionItem: CollectionEntry): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(collectionItem: List<CollectionEntry>)

    @Query("SELECT * FROM collection ORDER BY added DESC")
    fun getCollection() : DataSource.Factory<Int, CollectionItem>

    @Query("SELECT * FROM collection ORDER BY added DESC")
    suspend fun getCollectionSuspendable() : List<CollectionItem>

    @Query("SELECT * FROM collection WHERE show_id = :showId LIMIT 1")
    fun getCollectionItemLiveData(showId: Int): LiveData<CollectionEntry>

    @Query("SELECT * FROM collection WHERE show_id = :showId LIMIT 1")
    suspend fun getCollectionItem(showId: Int): CollectionEntry

    @Query("SELECT * FROM collection")
    fun getCollectionEntries() : LiveData<List<CollectionEntry>>

    @Query("SELECT * FROM collection ORDER BY added LIMIT :limit")
    fun getCollectionGridItems(limit: Int) : LiveData<List<MyShowGridItem>>

    @Query("SELECT * FROM collection ORDER BY added LIMIT :limit")
    fun getMyShowsGridItems(limit: Int) : List<MyShowGridItem>

    @Query("SELECT show_id  FROM collection WHERE show_id IN (:showIds)")
    suspend fun getIdsFromCollection(showIds: List<Int>) : List<Int>?

    @Query("DELETE FROM collection")
    fun deleteAll()

    @Query("DELETE FROM collection WHERE show_id = :showId")
    fun delete(showId: Int)
}