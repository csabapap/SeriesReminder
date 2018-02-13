package hu.csabapap.seriesreminder.data

import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import hu.csabapap.seriesreminder.data.db.daos.CollectionsDao
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import io.reactivex.Completable
import io.reactivex.Flowable
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
}