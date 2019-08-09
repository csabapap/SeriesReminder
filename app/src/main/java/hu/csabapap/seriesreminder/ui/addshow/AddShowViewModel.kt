package hu.csabapap.seriesreminder.ui.addshow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.tasks.Task
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.Observable
import timber.log.Timber

class AddShowViewModel(
        private val showsRepository: ShowsRepository,
        private val taskExecutor: TaskExecutor,
        private val schedulers: AppRxSchedulers
) : ViewModel() {

    val showLiveData = MutableLiveData<AddShowState>()

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
