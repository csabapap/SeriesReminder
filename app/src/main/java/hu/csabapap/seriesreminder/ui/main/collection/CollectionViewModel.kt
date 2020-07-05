package hu.csabapap.seriesreminder.ui.main.collection

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import javax.inject.Inject

class CollectionViewModel @Inject constructor(collectionRepository: CollectionRepository)
    : ViewModel() {

    var collectionsLiveData =
            LivePagedListBuilder<Int, CollectionItem>(collectionRepository.getCollections(),
                    10).build()
}