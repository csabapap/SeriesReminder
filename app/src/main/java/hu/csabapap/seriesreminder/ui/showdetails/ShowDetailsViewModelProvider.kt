package hu.csabapap.seriesreminder.ui.showdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import javax.inject.Inject

class ShowDetailsViewModelProvider @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val collectionRepository: CollectionRepository,
        private val dispatchers: AppCoroutineDispatchers)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShowDetailsViewModel(showsRepository, collectionRepository, dispatchers) as T
    }
}