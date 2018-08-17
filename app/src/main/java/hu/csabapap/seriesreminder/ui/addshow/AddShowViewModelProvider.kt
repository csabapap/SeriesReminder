package hu.csabapap.seriesreminder.ui.addshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import javax.inject.Inject
import javax.inject.Named

class AddShowViewModelProvider @Inject constructor(
        @param:Named("add_show_id") val showId: Int,
        private val showsRepository: ShowsRepository,
        private val collectionRepository: CollectionRepository,
        private val rxSchedulers: AppRxSchedulers

): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddShowViewModel(showId, showsRepository, collectionRepository, rxSchedulers ) as T
    }
}