package hu.csabapap.seriesreminder.data

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import hu.csabapap.seriesreminder.data.db.daos.CollectionsDao
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.data.network.TraktApi
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class CollectionRepository @Inject constructor(private val collectionsDao: CollectionsDao) {

    fun addToCollection(item: CollectionEntry) : Completable {
        return Completable.fromCallable({
            collectionsDao.insert(item)
        })
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
}