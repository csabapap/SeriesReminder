package hu.csabapap.seriesreminder.ui.addshow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.tasks.Task
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddShowViewModel(
        private val showsRepository: ShowsRepository,
        private val taskExecutor: TaskExecutor,
        private val dispatchers: AppCoroutineDispatchers
): ViewModel() {

    val showLiveData = MutableLiveData<AddShowState>()
    private val _uiState = MutableStateFlow<AddShowState>(Loading)
    val uiState: StateFlow<AddShowState>
        get() = _uiState

    fun getShow(showId: Int) {
        viewModelScope.launch(dispatchers.io) {
            val show = showsRepository.getShow(showId)
            withContext(dispatchers.main) {
                if (show != null) {
                    _uiState.value = DisplayShow(show)
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
