package hu.csabapap.seriesreminder.ui.main.collection

import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import javax.inject.Inject

class CollectionViewModel @Inject constructor(
        private val collectionRepository: CollectionRepository,
        private val schedulers: AppRxSchedulers)
    : ViewModel() {

    var collectionsLiveData =
            LivePagedListBuilder<Int, CollectionItem>(collectionRepository.getCollections(),
                    10).build()
}