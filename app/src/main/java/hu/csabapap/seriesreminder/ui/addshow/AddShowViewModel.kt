package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.Observable
import timber.log.Timber

class AddShowViewModel(
        private val showId: Int,
        private val showsRepository: ShowsRepository,
        private val collectionRepository: CollectionRepository,
        private val schedulers: AppRxSchedulers
) : ViewModel() {

    val isAdded: LiveData<Boolean> = MutableLiveData<Boolean>()
    val showLiveData = MutableLiveData<AddShowState>()
    val addShowLiveData = MutableLiveData<Boolean>()

    init {
        Timber.d("show id: $showId")
//        val show = collectionRepository.getShowFromCollection()
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
                    if (it.cover.isEmpty().not()) {
                        Observable.just(it)
                    } else {
                        showsRepository.images(it.tvdbId, "fanart")
                                .toObservable()
                                .flatMap { (data) ->
                                    val image = data
                                            .maxBy { it.ratingsInfo }
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



    fun addShowToCollection(showId: Int) {
        collectionRepository.addToCollection(CollectionEntry(showId = showId))
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .subscribe({
                    addShowLiveData.value = true
                }, {
                    addShowLiveData.value = false
                })
    }

}
