package hu.csabapap.seriesreminder.ui.addshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import javax.inject.Inject

class AddShowViewModelProvider @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val taskExecutor: TaskExecutor,
        private val dispatchers: AppCoroutineDispatchers
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AddShowViewModel(showsRepository, taskExecutor, dispatchers) as T
    }
}