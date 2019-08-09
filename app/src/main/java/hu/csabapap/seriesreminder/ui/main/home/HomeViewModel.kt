package hu.csabapap.seriesreminder.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.data.repositories.popularshows.PopularShowsRepository
import hu.csabapap.seriesreminder.data.repositories.trendingshows.TrendingShowsRepository
import hu.csabapap.seriesreminder.domain.SyncShowsUseCase
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val trendingShowsRepository: TrendingShowsRepository,
                                        private val popularShowsRepository: PopularShowsRepository,
                                        collectionRepository: CollectionRepository,
                                        private val episodesRepository: EpisodesRepository,
                                        private val synchShowsUseCase: SyncShowsUseCase,
                                        private val rxSchedulers: AppRxSchedulers,
                                        private val dispatchers: AppCoroutineDispatchers)
    : ViewModel() {

    private val disposables = CompositeDisposable()
    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)
    private val _viewStateLiveData = MutableLiveData<HomeViewState>()
    val viewStateLiveData: LiveData<HomeViewState>
        get() = _viewStateLiveData

    init {
        getTrendingShows()
        getPopularShows()
        refresh()
    }

    val upcomingEpisodesLiveData = MutableLiveData<List<NextEpisodeItem>>()

    val myShowsLiveData: LiveData<List<ShowItem>> = Transformations.map(collectionRepository.getCollectionGridItems()) {
        result -> result.map {
            ShowItem(it.show!!.traktId,
                    it.show!!.tvdbId,
                    it.show!!.title,
                    it.show!!.posterThumb,
                    true)
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
                .distinctUntilChanged()
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
                    Timber.d("nmb of show items: %d", showItems.size)
                    _viewStateLiveData.value = TrendingState(showItems)
                }, Timber::e))

    }

    private fun getPopularShows() {
        _viewStateLiveData.value = DisplayPopularLoader
        disposables.add(popularShowsRepository.getPopularShowsFlowable()
                .distinctUntilChanged()
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
                    _viewStateLiveData.value = PopularState(showItems)
                }, Timber::e))
    }

    private fun refresh() {
        scope.launch(dispatchers.io) {
            val trendingShows = trendingShowsRepository.refreshTrendingShow()
            withContext(dispatchers.main) {
                Timber.d("nmb of shows loaded: %d", trendingShows.size)
            }
        }

        scope.launch(dispatchers.io) {
            val popularShows = popularShowsRepository.refreshShows()
            withContext(dispatchers.main) {
                Timber.d("nmb of popular shows loaded: %d", popularShows.size)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun syncShows() {
        scope.launch(dispatchers.io) {
            synchShowsUseCase.syncShows()
        }
    }
}