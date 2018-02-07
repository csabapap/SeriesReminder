package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class AddShowViewModel @Inject constructor(
        private val showsRepository: ShowsRepository,
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
                    if (it.cover.isEmpty().not()) {
                        Observable.just(it)
                    } else {
                        showsRepository.images(it.tvdbId, "fanart")
                                .toObservable()
                                .flatMap { (data) ->
                                    val image = data
                                            .maxBy { it.ratings }
                                    image?.apply {
                                        it.coverThumb = thumbnail
                                        it.cover = fileName
                                        showsRepository.updateShow(it)
                                    }
                                    Observable.just(it)
                                }
                    }
                }
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .subscribe({
                    showLiveData.value = AddShowState(it)
                },
                        { Timber.e(it)})
    }

}
