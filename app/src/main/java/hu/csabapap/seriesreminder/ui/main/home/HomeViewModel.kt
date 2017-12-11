package hu.csabapap.seriesreminder.ui.main.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val showsRepository: ShowsRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val viewState = MutableLiveData<HomeViewState>()

    val trendingShowsLiveData = MutableLiveData<List<SRShow>>()
    val popularShowsLiveData = MutableLiveData<List<SRShow>>()

    init {
        viewState.value = HomeViewState(displayProgressBar = true)
    }

    fun getTrendingShows() {
         val disposable = showsRepository.getTrendingShows()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.value = currentViewState().copy(displayProgressBar = false, displayTrendingCard = true)

                    // TODO display trending shows
                },{})

        compositeDisposable.add(disposable)
    }

    fun getPopularShows() {
        val disposable = showsRepository.popularShows()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    viewState.value = currentViewState().copy(displayProgressBar = false, displayPopularCard = true)
                    popularShowsLiveData.value = it
                },
                        {t: Throwable? -> Timber.e(t) })

        compositeDisposable.add(disposable)
    }

    private fun currentViewState() : HomeViewState {
        return viewState.value!!
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}