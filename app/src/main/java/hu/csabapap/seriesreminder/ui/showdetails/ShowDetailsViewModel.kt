package hu.csabapap.seriesreminder.ui.showdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.getDateTimeForNextAir
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import timber.log.Timber

class ShowDetailsViewModel(private val showsRepository: ShowsRepository,
                           private val collectionRepository: CollectionRepository,
                           private val dispatchers: AppCoroutineDispatchers): ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _detailsUiState = MutableLiveData<ShowDetailsState>()
    val detailsUiState: LiveData<ShowDetailsState>
        get() = _detailsUiState

    fun getShow(showId: Int) {
        showsRepository.getShow(showId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.apply {
                        _detailsUiState.value = ShowDetailsState.Show(this)
                    }
                }, Timber::e)
    }

    fun createReminder(showId: Int) {
        scope.launch(dispatchers.io) {
            val show = showsRepository.getShow(showId).await()
            // TODO add reminder to db

            val day = show!!.airingTime.day
            val hours = show.airingTime.time
            val airDate = getDateTimeForNextAir(OffsetDateTime.now(ZoneOffset.UTC), day, hours)
            withContext(dispatchers.main) {
                _detailsUiState.value = ShowDetailsState.Reminder(show, airDate)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun removeFromCollection(showId: Int) {
        scope.launch(dispatchers.io) {
            collectionRepository.remove(showId)
        }
    }
}