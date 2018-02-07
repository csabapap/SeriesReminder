package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.CollectionsDao
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

class CollectionRepository @Inject constructor(private val collectionsDao: CollectionsDao) {

    fun addToCollection(item: CollectionItem) : Completable {
        return Completable.fromCallable({
            collectionsDao.insert(item)
        })
    }

}