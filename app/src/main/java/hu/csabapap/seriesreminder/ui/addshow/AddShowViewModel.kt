package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.Flowable
import io.reactivex.Observable
import timber.log.Timber
import java.util.*
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
                    if (it._coverThumb.isEmpty().not()) {
                        Timber.d(it._coverThumb)
                        Observable.just(it)
                    } else {
                        showsRepository.images(it.tvdbId, "fanart")
                                .toObservable()
                                .flatMap { (data) ->
                                    val image = data
                                            .maxBy { it.ratings }
                                    image?.apply {
                                        it._coverThumb = thumbnail
                                        it._cover = fileName
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
