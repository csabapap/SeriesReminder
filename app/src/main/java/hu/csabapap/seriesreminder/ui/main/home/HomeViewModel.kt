package hu.csabapap.seriesreminder.ui.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uwetrottmann.trakt5.TraktV2
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import hu.csabapap.seriesreminder.data.repositories.popularshows.PopularShowsRepository
import hu.csabapap.seriesreminder.data.repositories.trendingshows.TrendingShowsRepository
import hu.csabapap.seriesreminder.domain.SetEpisodeWatchedUseCase
import hu.csabapap.seriesreminder.domain.SyncShowsUseCase
import hu.csabapap.seriesreminder.domain.SyncWatchedShowsUseCase
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val trendingShowsRepository: TrendingShowsRepository,
                                        private val popularShowsRepository: PopularShowsRepository,
                                        private val collectionRepository: CollectionRepository,
                                        private val episodesRepository: EpisodesRepository,
                                        private val setEpisodeWatchedUseCase: SetEpisodeWatchedUseCase,
                                        private val syncShowsUseCase: SyncShowsUseCase,
                                        private val loggedInUserRepository: LoggedInUserRepository,
                                        private val syncWatchedShowsUseCase: SyncWatchedShowsUseCase,
                                        private val traktV2: TraktV2,
                                        private val dispatchers: AppCoroutineDispatchers)
    : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)
    private val _viewStateLiveData = MutableLiveData<HomeViewState>()

    private val _uiState = MutableStateFlow<HomeViewState>(InitialState)
    val uiState: StateFlow<HomeViewState>
        get() = _uiState
    var lastContentLoadedState: ContentLoaded = ContentLoaded()

    init {
        getTrendingShows()
        getPopularShows()
        refresh()
        syncShows()
    }

    val upcomingEpisodesLiveData = MutableLiveData<List<EpisodeWithShow>>()

    fun getShows() {
        getTrendingShows()
        getPopularShows()
        getCollection()
    }

    private fun getCollection() {
        viewModelScope.launch {
            val items = collectionRepository.getCollectionsSuspendable()
                .map {
                    ShowItem(it.show!!.traktId,
                        it.show!!.tvdbId,
                        it.show!!.title,
                        it.show!!.posterThumb,
                        true)
                }
            withContext(dispatchers.main) {
                updateUiState(lastContentLoadedState.copy(myShows = items))
            }
        }
    }

    fun getUpcomingEpisodes() {
        viewModelScope.launch {
            episodesRepository.getUpcomingEpisodesFlow()
                    .collect { upcomingEpisodes ->
                        if (upcomingEpisodes.isNotEmpty()) {
                            upcomingEpisodesLiveData.value = upcomingEpisodes
                        }
                    }
        }
    }

    fun getNextEpisodes() {
        viewModelScope.launch {
            val nextEpisodes: List<SRNextEpisode> = episodesRepository.getNextEpisodes()
            if (nextEpisodes.isNotEmpty()) {
                updateUiState(lastContentLoadedState.copy(nextEpisodes = nextEpisodes))
            }
        }
    }

    private fun getTrendingShows() {
        _viewStateLiveData.value = DisplayTrendingLoader

        viewModelScope.launch {
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
                    .collect { trendingShows ->
                        if (trendingShows != null) {
                            updateUiState(lastContentLoadedState.copy(trendingShows = trendingShows))
                        }
                    }
        }
    }

    private fun getPopularShows() {
        _viewStateLiveData.value = DisplayPopularLoader
        viewModelScope.launch  {
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
                    .collect {popularShows ->
                        updateUiState(lastContentLoadedState.copy(popularShows = popularShows))
                    }
        }
    }

    private fun refresh() {
        scope.launch(dispatchers.io) {
            val trendingShowsResult = trendingShowsRepository.refreshTrendingShow()
            withContext(dispatchers.main) {
                if (trendingShowsResult is Result.Success) {
                    Timber.d("nmb of shows loaded: %d", trendingShowsResult.data.size)
                } else {
                    _viewStateLiveData.postValue(HideTrendingSection)
                }
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

    private fun updateUiState(state: HomeViewState) {
        _uiState.value = state.also {
            if (it is ContentLoaded) {
                lastContentLoadedState = it
            }
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

    fun syncWatchedShows() {
        scope.launch(dispatchers.io) {
            if (!loggedInUserRepository.isLoggedIn()) return@launch
            traktV2.apply {
                accessToken(loggedInUserRepository.loggedInUser()?.accessToken)
                refreshToken(loggedInUserRepository.loggedInUser()?.refreshToken)
            }
            syncWatchedShowsUseCase.sync()
        }
    }
}