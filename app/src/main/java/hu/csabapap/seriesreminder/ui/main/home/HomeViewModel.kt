package hu.csabapap.seriesreminder.ui.main.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.EpisodesRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val showsRepository: ShowsRepository,
                                        private val episodesRepository: EpisodesRepository)
    : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val viewState = MutableLiveData<HomeViewState>()

    val trendingShowsLiveData = MutableLiveData<List<ShowItem>>()
    val popularShowsLiveData = MutableLiveData<List<ShowItem>>()
    val upcomingEpisodesLiveData = MutableLiveData<List<SREpisode>>()

    init {
        viewState.value = HomeViewState(displayProgressBar = true)
        compositeDisposable += showsRepository.getRemoteTrendingShows()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {Timber.e(it)})

        compositeDisposable += showsRepository.getPopularShowsFromWeb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {Timber.e(it)})
    }

    fun getTrendingShows() {
         val disposable = showsRepository.getTrendingShows()
                 .flatMap {
                     val showItems : MutableList<ShowItem> = mutableListOf()
                     it
                             .map {
                                 ShowItem(it.show!!.traktId,
                                         it.show!!.title,
                                         it.show!!.posterThumb,
                                         R.drawable.ic_watchers,
                                         it.entry?.watchers.toString())
                             }
                             .forEach { showItems.add(it) }
                     Flowable.just(showItems)
                 }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isEmpty().not()) {
                         viewState.value = currentViewState().copy(displayProgressBar = false, displayTrendingCard = true)
                        trendingShowsLiveData.value = it
                    }
                },{Timber.e(it)})

        compositeDisposable.add(disposable)
    }

    fun getPopularShows() {
        val disposable = showsRepository.popularShows()
                .flatMap {
                    val showItems : MutableList<ShowItem> = mutableListOf()
                    it
                            .map { ShowItem(it.show!!.traktId, it.show!!.title, it.show!!.posterThumb) }
                            .forEach { showItems.add(it) }
                    Flowable.just(showItems)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    if (it.isEmpty().not()) {
                        viewState.value = currentViewState().copy(displayProgressBar = false, displayPopularCard = true)
                        popularShowsLiveData.value = it
                    }
                },
                        {t: Throwable? -> Timber.e(t) })

        compositeDisposable.add(disposable)
    }

    fun getNextEpisodes() {
        episodesRepository.getNextEpisodes()
                .map {
                    val episodes = mutableListOf<SREpisode>()
                    it.forEach { nextEpisode ->
                        nextEpisode.episode?.let { srEpisode ->
                            episodes.add(srEpisode)
                        }
                    }
                    episodes
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( { nextEpisodes ->
                    Timber.d("number of next episodes: ${nextEpisodes.size}")
                    if (nextEpisodes.isEmpty().not()) {
                        upcomingEpisodesLiveData.value = nextEpisodes
                    }
                }, {Timber.e(it)})
    }

    private fun currentViewState() : HomeViewState {
        return viewState.value!!
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}