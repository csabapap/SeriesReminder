package hu.csabapap.seriesreminder.ui.showdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.notifications.NotificationsRepository
import hu.csabapap.seriesreminder.data.repositories.relatedshows.RelatedShowsRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.domain.CreateNotificationAlarmUseCase
import hu.csabapap.seriesreminder.domain.SetEpisodeWatchedUseCase
import hu.csabapap.seriesreminder.extensions.distinctUntilChanged
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ShowDetailsViewModel(private val showsRepository: ShowsRepository,
                           private val seasonsRepository: SeasonsRepository,
                           private val episodesRepository: EpisodesRepository,
                           private val collectionRepository: CollectionRepository,
                           private val notificationsRepository: NotificationsRepository,
                           private val relatedShowsRepository: RelatedShowsRepository,
                           private val setEpisodeWatchedUseCase: SetEpisodeWatchedUseCase,
                           private val createNotificationAlarmUseCase: CreateNotificationAlarmUseCase,
                           private val dispatchers: AppCoroutineDispatchers) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _detailsUiState = MutableLiveData<ShowDetailsState>()
    val detailsUiState: LiveData<ShowDetailsState>
        get() = _detailsUiState

    fun getShow(showId: Int) {
        scope.launch(dispatchers.io) {
            val show = showsRepository.getShow(showId) ?: return@launch
            withContext(dispatchers.main) {
                _detailsUiState.value = ShowDetailsState.Show(show)
            }
        }
    }

    fun loadNextEpisode(showId: Int) {
        scope.launch(dispatchers.io) {
            val show = showsRepository.getShow(showId) ?: return@launch
            val nextEpisodeAbsNumber = if (show.nextEpisode == -1) {
                1
            } else {
                show.nextEpisode
            }
            val episode = getNextEpisode(showId, nextEpisodeAbsNumber)
            withContext(dispatchers.main) {
                if (episode != null) {
                    _detailsUiState.value = ShowDetailsState.NextEpisode(episode)
                } else {
                    _detailsUiState.value = ShowDetailsState.NextEpisodeNotFound
                }
            }
        }
    }

    private fun getSeasonsLiveData(showId: Int): LiveData<List<SRSeason>> {
        return seasonsRepository.getSeasonsLiveData(showId)
    }

    private suspend fun getNextEpisode(showId: Int, nextEpisodeAbsNumber: Int): SREpisode? {
        return episodesRepository.getEpisodeByAbsNumber(showId, nextEpisodeAbsNumber)
    }

    fun createNotification(showId: Int, aheadOfTime: Int) {
        scope.launch(dispatchers.io) {
            createNotificationAlarmUseCase.createReminderAlarm(showId, aheadOfTime)
            withContext(dispatchers.main) {
                _detailsUiState.value = ShowDetailsState.NotificationCreated
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun removeFromCollection(showId: Int) {
        scope.launch(dispatchers.io) {
            collectionRepository.remove(showId)
        }
    }

    fun getNotifications(showId: Int) {
        scope.launch(dispatchers.io) {
            val notification = notificationsRepository.getNotification(showId)

            withContext(dispatchers.main) {
                _detailsUiState.value = when (notification != null) {
                    true -> ShowDetailsState.Notification(notification)
                    else -> ShowDetailsState.AddNotificationButton
                }
            }
        }
    }

    fun removeNotification(showId: Int) {
        scope.launch(dispatchers.io) {
            createNotificationAlarmUseCase.cancelNotification(showId)
            withContext(dispatchers.main) {
                _detailsUiState.value = ShowDetailsState.NotificationDeleted
            }
        }
    }

    fun refreshRelatedShows(id: Int) {
        scope.launch(dispatchers.io) {
            try {
                relatedShowsRepository.refreshRelatedShows(id)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun setEpisodeWatched(episode: SREpisode) {
        scope.launch(dispatchers.io) {
            setEpisodeWatchedUseCase(episode)
            val nextEpisodeAbsNumber = episode.absNumber + 1
            val nextEpisode = getNextEpisode(episode.showId, nextEpisodeAbsNumber)
            withContext(dispatchers.main) {
                if (nextEpisode != null) {
                    _detailsUiState.value = ShowDetailsState.NextEpisode(nextEpisode)
                } else {
                    _detailsUiState.value = ShowDetailsState.NextEpisodeNotFound
                }
            }
        }
    }

    fun observeSeasons(showId: Int): LiveData<List<SRSeason>> {
        return getSeasonsLiveData(showId)
    }

    fun observeRelatedShows(showId: Int): LiveData<List<ShowItem>> {
        return Transformations.map(relatedShowsRepository.liveRelatedShows(showId)) {
            it.map itemMapper@ { relatedShow ->
                val srShow = relatedShow.show!!
                val inCollection = relatedShow.inCollection
                return@itemMapper ShowItem(srShow.traktId,
                        srShow.tvdbId,
                        srShow.title,
                        srShow.posterThumb,
                        inCollection)
            }
        }
                .distinctUntilChanged()
    }

    fun setWatchedAllEpisodeInSeason(season: SRSeason) {
        scope.launch(dispatchers.io) {
            val episodes = episodesRepository.getEpisodesBySeason(season.showId, season.number)
                    .associateBy { it.number }

            episodes.forEach {
                setEpisodeWatchedUseCase(it.value)
            }

            val lastEpisodeInSeason = episodes[episodes.size - 1] ?: return@launch
            val srShow = showsRepository.getShow(season.showId) ?: return@launch
            val nextEpisodeAbsNumber = srShow.nextEpisode
            val nextEpisode = getNextEpisode(lastEpisodeInSeason.showId, nextEpisodeAbsNumber)
            withContext(dispatchers.main) {
                if (nextEpisode != null) {
                    _detailsUiState.value = ShowDetailsState.NextEpisode(nextEpisode)
                } else {
                    _detailsUiState.value = ShowDetailsState.NextEpisodeNotFound
                }
            }
        }
    }
}