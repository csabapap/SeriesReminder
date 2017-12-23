package hu.csabapap.seriesreminder.ui.main.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val showsRepository: ShowsRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val viewState = MutableLiveData<HomeViewState>()

    val trendingShowsLiveData = MutableLiveData<List<TrendingGridItem>>()
    val popularShowsLiveData = MutableLiveData<List<PopularGridItem>>()

    init {
        viewState.value = HomeViewState(displayProgressBar = true)
        showsRepository.getRemoteTrendingShows()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {Timber.e(it)})

        showsRepository.getPopularShowsFromWeb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {Timber.e(it)})
    }

    fun getTrendingShows() {
         val disposable = showsRepository.getTrendingShows()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.value = currentViewState().copy(displayProgressBar = false, displayTrendingCard = true)
                    Timber.d("number of trending shows: ${it.size}")
                    trendingShowsLiveData.value = it
                },{Timber.e(it)})

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