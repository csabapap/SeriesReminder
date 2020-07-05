package hu.csabapap.seriesreminder.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.popularshows.PopularShowsRepository
import hu.csabapap.seriesreminder.data.repositories.trendingshows.TrendingShowsRepository
import hu.csabapap.seriesreminder.domain.SetEpisodeWatchedUseCase
import hu.csabapap.seriesreminder.domain.SyncShowsUseCase
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val trendingShowsRepository: TrendingShowsRepository,
                                        private val popularShowsRepository: PopularShowsRepository,
                                        collectionRepository: CollectionRepository,
                                        private val episodesRepository: EpisodesRepository,
                                        private val setEpisodeWatchedUseCase: SetEpisodeWatchedUseCase,
                                        private val syncShowsUseCase: SyncShowsUseCase,
                                        private val dispatchers: AppCoroutineDispatchers)
    : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)
    private val _viewStateLiveData = MutableLiveData<HomeViewState>()
    val viewStateLiveData: LiveData<HomeViewState>
        get() = _viewStateLiveData

    init {
        getTrendingShows()
        getPopularShows()
        refresh()
        syncShows()
    }

    val upcomingEpisodesLiveData = MutableLiveData<List<EpisodeWithShow>>()

    val myShowsLiveData: LiveData<List<ShowItem>> = Transformations.map(collectionRepository.getCollectionGridItems()) {
        result -> result.map {
            ShowItem(it.show!!.traktId,
                    it.show!!.tvdbId,
                    it.show!!.title,
                    it.show!!.posterThumb,
                    true)
        }
    }

    fun getUpcomingEpisodes() {
        scope.launch(dispatchers.main) {
            episodesRepository.getUpcomingEpisodesFlow()
                    .collect { upcomingEpisodes ->
                        if (upcomingEpisodes.isNotEmpty()) {
                            upcomingEpisodesLiveData.value = upcomingEpisodes
                        }
                    }
        }
    }

    fun getNextEpisodes() {
        scope.launch(dispatchers.io) {
            val nextEpisodes: List<SRNextEpisode> = episodesRepository.getNextEpisodes()
            withContext(dispatchers.main) {
                if (nextEpisodes.isNotEmpty()) {
                    _viewStateLiveData.value = NextEpisodesState(nextEpisodes)
                }
            }
        }
    }

    private fun getTrendingShows() {
        _viewStateLiveData.value = DisplayTrendingLoader

        scope.launch(dispatchers.main) {
            trendingShowsRepository.getTrendingShowsFlow()
                    .map { trendingShows ->
                        trendingShows?.map {
                            val show = it.show ?: return@map null
                            ShowItem(show.traktId,
                                    show.tvdbId,
                                    show.title,
                                    show.posterThumb,
                                    it.inCollection)
                        }?.filterNotNull()
                    }
                    .distinctUntilChanged()
                    .collect {
                        if (it != null) {
                            _viewStateLiveData.value = TrendingState(it)
                        }
                    }
        }
    }

    private fun getPopularShows() {
        _viewStateLiveData.value = DisplayPopularLoader
        scope.launch(dispatchers.main) {
            popularShowsRepository.getPopularShowsFlow()
                    .map { trendingShows ->
                        trendingShows.map {
                            val show = it.show ?: return@map null
                            ShowItem(show.traktId,
                                    show.tvdbId,
                                    show.title,
                                    show.posterThumb,
                                    it.inCollection)
                        }.filterNotNull()
                    }
                    .distinctUntilChanged()
                    .collect {
                        _viewStateLiveData.value = PopularState(it)
                    }
        }
    }

    private fun refresh() {
        scope.launch(dispatchers.io) {
            val trendingShowsResult = trendingShowsRepository.refreshTrendingShow()
            if (trendingShowsResult is Result.Success)
            withContext(dispatchers.main) {
                Timber.d("nmb of shows loaded: %d", trendingShowsResult.data.size)
            }
        }

        scope.launch(dispatchers.io) {
            val popularShows = popularShowsRepository.refreshShows()
            withContext(dispatchers.main) {
                Timber.d("nmb of popular shows loaded: %d", popularShows.size)
            }
        }
    }

    private fun syncShows() {
        scope.launch(dispatchers.io) {
            syncShowsUseCase.syncShows()
        }
    }

    fun setEpisodeWatched(nextEpisode: SRNextEpisode) {
        scope.launch(dispatchers.io) {
            val episode = episodesRepository.getEpisode(nextEpisode.showId, nextEpisode.season,
                    nextEpisode.number)
            if (episode != null) {
                setEpisodeWatchedUseCase(episode.episode)
                getNextEpisodes()
            }
        }
    }
}