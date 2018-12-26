package hu.csabapap.seriesreminder.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.db.daos.CollectionsDao
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.db.entities.MyShowGridItem
import hu.csabapap.seriesreminder.data.network.TraktApi
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class CollectionRepository @Inject constructor(private val collectionsDao: CollectionsDao) {

    fun addToCollection(item: CollectionEntry) : Completable {
        return Completable.fromCallable {
            collectionsDao.insert(item)
        }
    }

    fun save(item: CollectionEntry): Long {
        return collectionsDao.insert(item)
    }

    fun getCollections() : DataSource.Factory<Int, CollectionItem> {
        return collectionsDao.getCollection()
    }

    fun getCollectionsSingle() : Single<List<CollectionItem>> {
        return collectionsDao.getCollectionSingle()
    }

    fun getEntry(showId: Int): LiveData<CollectionEntry> {
        return collectionsDao.getCollectionItem(showId)
    }

    fun getCollectionItem(showId: Int): Single<CollectionEntry> {
        return collectionsDao.getCollectionItemSingle(showId)
    }

    fun getCollection(): LiveData<List<CollectionEntry>> {
        return collectionsDao.getCollectionEntries()
    }

    fun getItemsFromCollection(ids: List<Int>): Single<List<Int>> {
        return collectionsDao.getIdsFromCollection(ids)
    }

    fun getCollectionGridItems(limit: Int = 10): LiveData<List<MyShowGridItem>> {
        return collectionsDao.getCollectionGridItems(limit)
    }

    fun remove(showId: Int) {
        collectionsDao.delete(showId)
    }
}