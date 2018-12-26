package hu.csabapap.seriesreminder.data.db.daos

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.db.entities.MyShowGridItem
import io.reactivex.Single
import org.intellij.lang.annotations.Language

@Dao
interface CollectionsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(collectionItem: CollectionEntry): Long

    @Query("SELECT * FROM collection ORDER BY added DESC")
    fun getCollection() : DataSource.Factory<Int, CollectionItem>

    @Query("SELECT * FROM collection ORDER BY added DESC")
    fun getCollectionSingle() : Single<List<CollectionItem>>

    @Query("SELECT * FROM collection WHERE show_id = :showId LIMIT 1")
    fun getCollectionItem(showId: Int): LiveData<CollectionEntry>

    @Query("SELECT * FROM collection WHERE show_id = :showId LIMIT 1")
    fun getCollectionItemSingle(showId: Int): Single<CollectionEntry>

    @Query("SELECT * FROM collection")
    fun getCollectionEntries() : LiveData<List<CollectionEntry>>

    @Query("SELECT * FROM collection ORDER BY added DESC LIMIT :limit")
    fun getCollectionGridItems(limit: Int) : LiveData<List<MyShowGridItem>>

    @Query("SELECT show_id  FROM collection WHERE show_id IN (:showIds)")
    fun getIdsFromCollection(showIds: List<Int>) : Single<List<Int>>

    @Query("DELETE FROM collection")
    fun deleteAll()

    @Query("DELETE FROM collection WHERE show_id = :showId")
    fun delete(showId: Int)
}