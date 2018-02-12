package hu.csabapap.seriesreminder.ui.main.collection

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import timber.log.Timber
import javax.inject.Inject

class CollectionViewModel @Inject constructor(
        private val collectionRepository: CollectionRepository,
        private val schedulers: AppRxSchedulers)
    : ViewModel() {

    var collectionsLiveData = MutableLiveData<List<CollectionItem>>()

    fun getCollection() {
        collectionRepository.getCollections()
                .subscribeOn(schedulers.database)
                .observeOn(schedulers.main)
                .subscribe({
                    collectionsLiveData.value = it
                    Timber.d("number of shows in collection> ${it.size}")
                }, {

                })
    }
}