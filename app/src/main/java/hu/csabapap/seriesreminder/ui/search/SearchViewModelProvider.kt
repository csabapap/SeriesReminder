package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import javax.inject.Inject

@Suppress( "UNCHECKED_CAST")
class SearchViewModelProvider @Inject constructor(
        private val api: TraktApi,
        private val showsRepository: ShowsRepository,
        private val collectionRepository: CollectionRepository,
        private val schedulers: AppRxSchedulers)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass != SearchViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return SearchViewModel(api, showsRepository, collectionRepository, schedulers) as T
    }
}