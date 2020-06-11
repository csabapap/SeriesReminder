package hu.csabapap.seriesreminder.ui.addshow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.tasks.Task
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddShowViewModel(
        private val showsRepository: ShowsRepository,
        private val taskExecutor: TaskExecutor,
        private val dispatchers: AppCoroutineDispatchers
): ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    val showLiveData = MutableLiveData<AddShowState>()

    fun getShow(showId: Int) {
        scope.launch(dispatchers.io) {
            val show = showsRepository.getShow(showId)
            withContext(dispatchers.main) {
                if (show != null) {
                    showLiveData.value = DisplayShow(show)
                }
            }
        }
    }

    fun syncShow(task: Task) {
        taskExecutor.queue.add(task)
    }

    fun onBackButtonClick() {
        showLiveData.value = Close
    }
}
