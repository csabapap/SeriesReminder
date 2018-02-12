package hu.csabapap.seriesreminder.data

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

    fun getCollections() : Flowable<List<CollectionItem>> {
        return collectionsDao.getCollection(30)
    }
}