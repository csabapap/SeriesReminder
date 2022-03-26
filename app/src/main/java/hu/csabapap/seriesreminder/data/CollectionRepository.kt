package hu.csabapap.seriesreminder.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import hu.csabapap.seriesreminder.data.db.daos.CollectionsDao
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.db.entities.MyShowGridItem
import javax.inject.Inject

class CollectionRepository @Inject constructor(private val collectionsDao: CollectionsDao) {

    fun save(item: CollectionEntry): Long {
        return collectionsDao.insert(item)
    }

    fun saveAll(items: List<CollectionEntry>) {
        return collectionsDao.insertAll(items)
    }

    fun getCollections() : DataSource.Factory<Int, CollectionItem> {
        return collectionsDao.getCollection()
    }

    suspend fun getCollectionsSuspendable() : List<CollectionItem> {
        return collectionsDao.getCollectionSuspendable()
    }

    suspend fun getCollectionItem(showId: Int): CollectionEntry {
        return collectionsDao.getCollectionItem(showId)
    }

    suspend fun getItemsFromCollection(ids: List<Int>): List<Int>? {
        return collectionsDao.getIdsFromCollection(ids)
    }

    fun getCollectionGridItems(limit: Int = 10): LiveData<List<MyShowGridItem>> {
        return collectionsDao.getCollectionGridItems(limit)
    }

    fun remove(showId: Int) {
        collectionsDao.delete(showId)
    }
}