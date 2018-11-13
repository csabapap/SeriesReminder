package hu.csabapap.seriesreminder.ui.addshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.tasks.Task
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.Observable
import org.threeten.bp.OffsetDateTime
import timber.log.Timber

class AddShowViewModel(
        showId: Int,
        private val showsRepository: ShowsRepository,
        private val collectionRepository: CollectionRepository,
        private val taskExecutor: TaskExecutor,
        private val schedulers: AppRxSchedulers
) : ViewModel() {

    var isAdded: LiveData<Boolean> = MutableLiveData<Boolean>()
    val showLiveData = MutableLiveData<AddShowState>()
    val addShowLiveData = MutableLiveData<Boolean>()

    init {
        val collectionEntry = collectionRepository.getEntry(showId)
        isAdded = Transformations.map(collectionEntry) {it != null}
    }

    fun getShow(showId: Int) {
        showsRepository.getShow(traktId = showId)
                .observeOn(schedulers.main)
                .doAfterSuccess({
                    showLiveData.value = AddShowState(it)
                })
                .observeOn(schedulers.io)
                .toObservable()
                .flatMap {
                    Observable.just(it)
                }
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .subscribe({
                    showLiveData.value = AddShowState(it)
                },
                        { Timber.e(it)})
    }

    fun syncShow(task: Task) {
        taskExecutor.queue.add(task)
    }

}
