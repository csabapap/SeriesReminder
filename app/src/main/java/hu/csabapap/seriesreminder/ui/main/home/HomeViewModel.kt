package hu.csabapap.seriesreminder.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.EpisodesRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.data.repositories.trendingshows.TrendingShowsRepository
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val showsRepository: ShowsRepository,
                                        trendingShowsRepository: TrendingShowsRepository,
                                        collectionRepository: CollectionRepository,
                                        private val episodesRepository: EpisodesRepository,
                                        private val rxSchedulers: AppRxSchedulers)
    : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val viewState = MutableLiveData<HomeViewState>()

    val popularShowsLiveData = MutableLiveData<List<ShowItem>>()
    val upcomingEpisodesLiveData = MutableLiveData<List<NextEpisodeItem>>()

    val trendingShows: LiveData<List<ShowItem>> = Transformations.map(trendingShowsRepository.getTrendingShows().data) { result ->
        result.map {
            ShowItem(it.show!!.traktId,
                    it.show!!.tvdbId,
                    it.show!!.title,
                    it.show!!.posterThumb,
                    it.show!!.inCollection)
        }
    }

    val myShowsLiveData: LiveData<List<ShowItem>> = Transformations.map(collectionRepository.getCollectionGridItems()) {
        result -> result.map {
            ShowItem(it.show!!.traktId,
                    it.show!!.tvdbId,
                    it.show!!.title,
                    it.show!!.posterThumb,
                    it.show!!.inCollection)
        }
    }

    init {
        viewState.value = HomeViewState(displayProgressBar = true)

        compositeDisposable += showsRepository.getPopularShowsFromWeb()
                .subscribeOn(rxSchedulers.io)
                .observeOn(rxSchedulers.main)
                .subscribe({}, {Timber.e(it)})
    }

    fun getPopularShows() {
        val disposable = showsRepository.popularShows()
                .flatMap {
                    val showItems : MutableList<ShowItem> = mutableListOf()
                    it
                            .map { ShowItem(it.show!!.traktId, it.show!!.tvdbId, it.show!!.title, it.show!!.posterThumb) }
                            .forEach { showItems.add(it) }
                    Flowable.just(showItems)
                }
                .subscribeOn(rxSchedulers.io)
                .observeOn(rxSchedulers.main)
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
        val disposable = episodesRepository.getNextEpisodes(3)
                .subscribeOn(rxSchedulers.io)
                .observeOn(rxSchedulers.main)
                .subscribe( { nextEpisodes ->
                    if (nextEpisodes.isEmpty().not()) {
                        upcomingEpisodesLiveData.value = nextEpisodes
                    }
                }, {Timber.e(it)})
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