package hu.csabapap.seriesreminder.ui.addshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import javax.inject.Inject
import javax.inject.Named

class AddShowViewModelProvider @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val taskExecutor: TaskExecutor,
        private val rxSchedulers: AppRxSchedulers

): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddShowViewModel(showsRepository, taskExecutor, rxSchedulers) as T
    }
}