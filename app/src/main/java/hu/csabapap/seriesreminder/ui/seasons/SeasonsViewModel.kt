package hu.csabapap.seriesreminder.ui.seasons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class SeasonsViewModel(private val showsRepository: ShowsRepository,
                       private val dispatchers: AppCoroutineDispatchers) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _detailsUiState = MutableLiveData<SeasonsUiState>()
    val detailsUiState: LiveData<SeasonsUiState>
        get() = _detailsUiState

    fun getShow(showId: Int) {
        scope.launch(dispatchers.io) {
            val show = showsRepository.getShow(showId).await()

            withContext(dispatchers.main) {
                if (show != null) {
                    _detailsUiState.value = SeasonsUiState.DisplayShow(show)
                }
            }
        }
    }
}