package hu.csabapap.seriesreminder.data.db.daos

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import org.intellij.lang.annotations.Language

@Dao
interface CollectionsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(collectionItem: CollectionEntry)

    @Language("RoomSql")
    @Query("SELECT * FROM collection")
    fun getCollection() : DataSource.Factory<Int, CollectionItem>

    @Query("DELETE FROM collection")
    fun deleteAll()
}