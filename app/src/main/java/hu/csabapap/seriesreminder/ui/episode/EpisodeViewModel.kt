package hu.csabapap.seriesreminder.ui.episode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.domain.RemoveEpisodeFromWatchedUseCase
import hu.csabapap.seriesreminder.domain.SetEpisodeWatchedUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EpisodeViewModel @Inject constructor(
        private val episodesRepository: EpisodesRepository,
        private val setEpisodeWatchedUseCase: SetEpisodeWatchedUseCase,
        private val removeEpisodeFromWatchedUseCase: RemoveEpisodeFromWatchedUseCase,
        private val dispatchers: AppCoroutineDispatchers): ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableLiveData<EpisodeUiState>()
    val uiState: LiveData<EpisodeUiState>
        get() = _uiState

    fun getEpisode(showId: Int, seasonNumber: Int, episodeNumber: Int) {
        scope.launch {
            val episode = episodesRepository.getEpisode(showId, seasonNumber, episodeNumber)

            withContext(dispatchers.main) {
                if (episode != null) {
                    _uiState.value = EpisodeUiState.DisplayEpisode(episode)
                }
            }
        }
    }

    fun setWatched(showId: Int, seasonNumber: Int, episodeNumber: Int) {
        scope.launch(dispatchers.io) {
            val episode = episodesRepository.getEpisode(showId, seasonNumber, episodeNumber)
            if (episode != null) {
                setEpisodeWatchedUseCase(episode.episode)
            }
        }
    }

    fun removeFromWatched(showId: Int, seasonNumber: Int, episodeNumber: Int) {
        scope.launch(dispatchers.io) {
            val watchedEpisode = WatchedEpisode(showId = showId, season = seasonNumber, number =  episodeNumber, episodeId = -1)
            removeEpisodeFromWatchedUseCase(watchedEpisode)
        }
    }
}