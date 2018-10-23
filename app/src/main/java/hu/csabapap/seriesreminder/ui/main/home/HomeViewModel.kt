package hu.csabapap.seriesreminder.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.EpisodesRepository
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.data.repositories.popularshows.PopularShowsRepository
import hu.csabapap.seriesreminder.data.repositories.trendingshows.TrendingShowsRepository
import hu.csabapap.seriesreminder.extensions.distinctUntilChanged
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val trendingShowsRepository: TrendingShowsRepository,
                                        popularShowsRepository: PopularShowsRepository,
                                        collectionRepository: CollectionRepository,
                                        private val episodesRepository: EpisodesRepository,
                                        private val rxSchedulers: AppRxSchedulers)
    : ViewModel() {

    private val disposables = CompositeDisposable()
    private val _viewStateLiveData = MutableLiveData<HomeViewState>()
    val viewStateLiveData: LiveData<HomeViewState>
        get() = _viewStateLiveData.distinctUntilChanged()

    init {
        getTrendingShows()
        refresh()
    }

    val upcomingEpisodesLiveData = MutableLiveData<List<NextEpisodeItem>>()


    val popularShows: LiveData<List<ShowItem>> = Transformations.map(popularShowsRepository.getPopularShows().data.distinctUntilChanged()) { result ->
        result.map {
            ShowItem(it.show!!.traktId,
                    it.show!!.tvdbId,
                    it.show!!.title,
                    it.show!!.posterThumb,
                    it.inCollection)
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

    fun getNextEpisodes() {
        val disposable = episodesRepository.getNextEpisodes(3)
                .subscribeOn(rxSchedulers.io)
                .observeOn(rxSchedulers.main)
                .subscribe( { nextEpisodes ->
                    if (nextEpisodes.isEmpty().not()) {
                        upcomingEpisodesLiveData.value = nextEpisodes
                    }
                }, {Timber.e(it)})
        disposables.add(disposable)
    }

    private fun getTrendingShows() {
        _viewStateLiveData.value = DisplayTrendingLoader
        disposables.add(trendingShowsRepository.getTrendingShowsFlowable()
                .map { gridItems ->
                    gridItems.map {
                        ShowItem(it.show!!.traktId,
                                it.show!!.tvdbId,
                                it.show!!.title,
                                it.show!!.posterThumb,
                                it.inCollection)
                    }
                }
                .subscribeOn(rxSchedulers.io)
                .observeOn(rxSchedulers.main)
                .subscribe({ showItems ->
                    _viewStateLiveData.value = TrendingState(showItems)
                }, Timber::e))

    }

    private fun refresh() {
        disposables.add(trendingShowsRepository.refreshTrendingShows()
                .subscribeOn(rxSchedulers.io)
                .observeOn(rxSchedulers.main)
                .subscribe({ showItems ->
                    Timber.d("nmb of shows loaded: %d", showItems.size)
                }, Timber::e))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}