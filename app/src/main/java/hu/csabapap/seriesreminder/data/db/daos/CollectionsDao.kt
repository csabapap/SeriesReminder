package hu.csabapap.seriesreminder.data.db.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import io.reactivex.Flowable
import org.intellij.lang.annotations.Language

@Dao
interface CollectionsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(collectionItem: CollectionEntry)

    @Language("RoomSql")
    @Query("SELECT * FROM collection LIMIT :limit")
    fun getCollection(limit: Int) : Flowable<List<CollectionItem>>

    @Query("DELETE FROM collection")
    fun deleteAll()
}