package hu.csabapap.seriesreminder.ui.seasons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.repositories.WatchedEpisodesRepository
import hu.csabapap.seriesreminder.domain.RemoveEpisodeFromWatchedUseCase
import hu.csabapap.seriesreminder.domain.SetEpisodeWatchedUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime

class SeasonsViewModel(private val showsRepository: ShowsRepository,
                       private val watchedEpisodesRepository: WatchedEpisodesRepository,
                       private val setEpisodeWatchedUseCase: SetEpisodeWatchedUseCase,
                       private val removeEpisodeFromWatchedUseCase: RemoveEpisodeFromWatchedUseCase,
                       private val dispatchers: AppCoroutineDispatchers) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _detailsUiState = MutableLiveData<SeasonsUiState>()
    val detailsUiState: LiveData<SeasonsUiState>
        get() = _detailsUiState

    fun getShow(showId: Int) {
        scope.launch(dispatchers.io) {
            val show = showsRepository.getShow(showId).await()

            withContext(dispatchers.main) {
                if (show != null) {
                    _detailsUiState.value = SeasonsUiState.DisplayShow(show)
                }
            }
        }
    }

    fun getSeasonWithEpisodes(showId: Int, seasonNumber: Int) {
        scope.launch(dispatchers.io) {
            val episodes = watchedEpisodesRepository.getEpisodesWithWatchedData(showId, seasonNumber)
            if (episodes != null) {
                val episodeItems = episodes.map {
                    EpisodeItem(it.episode, it.watched())
                }
                withContext(dispatchers.main) {
                    _detailsUiState.value = SeasonsUiState.DisplayEpisodes(episodeItems)
                }
            }
        }
    }

    fun setEpisodeAsWatched(episode: SREpisode) {
        scope.launch(dispatchers.io) {
            setEpisodeWatchedUseCase(episode)
        }
    }

    fun removeEpisodeFromWatched(episode: SREpisode) {
        scope.launch(dispatchers.io) {
            val watchedEpisode = WatchedEpisode(null, episode.showId, episode.season, episode.number, episode.id ?: -1, OffsetDateTime.now())
            removeEpisodeFromWatchedUseCase(watchedEpisode)
        }
    }
}