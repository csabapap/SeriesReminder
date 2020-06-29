package hu.csabapap.seriesreminder.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import hu.csabapap.seriesreminder.data.db.daos.CollectionsDao
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.db.entities.MyShowGridItem
import io.reactivex.Single
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class CollectionRepository @Inject constructor(private val collectionsDao: CollectionsDao) {

    fun save(item: CollectionEntry): Long {
        return collectionsDao.insert(item)
    }

    fun getCollections() : DataSource.Factory<Int, CollectionItem> {
        return collectionsDao.getCollection()
    }

    suspend fun getCollectionsSuspendable() : List<CollectionItem> {
        return collectionsDao.getCollectionSuspendable()
    }

    fun getCollectionItem(showId: Int): Single<CollectionEntry> {
        return collectionsDao.getCollectionItemSingle(showId)
    }

    fun getItemsFromCollectionSingle(ids: List<Int>): Single<List<Int>> {
        return collectionsDao.getIdsFromCollectionSingle(ids)
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