package hu.csabapap.seriesreminder.ui.showdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.entities.SrNotification
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.repositories.WatchedEpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.notifications.NotificationsRepository
import hu.csabapap.seriesreminder.data.repositories.relatedshows.RelatedShowsRepository
import hu.csabapap.seriesreminder.extensions.distinctUntilChanged
import hu.csabapap.seriesreminder.services.workers.ShowReminderWorker
import hu.csabapap.seriesreminder.services.workers.SyncNextEpisodeWorker
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.Reminder
import hu.csabapap.seriesreminder.utils.getDateTimeForNextAir
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import timber.log.Timber
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class ShowDetailsViewModel(private val showsRepository: ShowsRepository,
                           private val seasonsRepository: SeasonsRepository,
                           private val episodesRepository: EpisodesRepository,
                           private val collectionRepository: CollectionRepository,
                           private val notificationsRepository: NotificationsRepository,
                           private val relatedShowsRepository: RelatedShowsRepository,
                           private val watchedEpisodesRepository: WatchedEpisodesRepository,
                           private val workManager: WorkManager,
                           private val dispatchers: AppCoroutineDispatchers) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _detailsUiState = MutableLiveData<ShowDetailsState>()
    val detailsUiState: LiveData<ShowDetailsState>
        get() = _detailsUiState

    fun getShowWithEpisode(showId: Int) {
        scope.launch(dispatchers.io) {
            val show = showsRepository.getShow(showId).await() ?: return@launch

            withContext(dispatchers.main) {
                _detailsUiState.value = ShowDetailsState.Show(show)
            }

            val nextEpisodeAbsNumber =  if (show.nextEpisode == -1) {
                1
            } else {
                show.nextEpisode
            }
            val episode = episodesRepository.getNextEpisode(showId, nextEpisodeAbsNumber)
            withContext(dispatchers.main) {
                if (episode != null) {
                    _detailsUiState.value = ShowDetailsState.NextEpisode(episode)
                } else {
                    _detailsUiState.value = ShowDetailsState.NextEpisodeNotFound
                }
            }

            val seasons = getSeasons(showId, show.tvdbId)
                    .map { season ->
                        if (season.thumbnail == null || season.thumbnail.isEmpty()) {
                            return@map season.copy(thumbnail = show.posterThumb)
                        }
                        season
                    }

            withContext(dispatchers.main) {
                _detailsUiState.value = ShowDetailsState.Seasons(seasons)
            }
        }
    }

    suspend fun getSeasons(showId: Int, showTvdbId: Int): List<SRSeason> {

        val seasons = seasonsRepository.getSeasonsFromDb(showId, showTvdbId) ?: emptyList()

        return seasons.filter {
            it.number > 0
        }

    }

    fun getNextEpisode() {

    }

    fun createNotification(showId: Int, aheadOfTime: Int) {
        scope.launch(dispatchers.io) {
            val show = showsRepository.getShow(showId).await()
//            val notification = SrNotification(null, showId, aheadOfTime, -1)
//            notificationsRepository.createNotification(notification)
            val day = show!!.airingTime.day
            val hours = show.airingTime.time
            val airDateTime = getDateTimeForNextAir(OffsetDateTime.now(ZoneOffset.UTC), day, hours)

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, airDateTime.dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, airDateTime.hour)
            calendar.set(Calendar.MINUTE, airDateTime.minute)
            calendar.set(Calendar.SECOND, 0)
            val duration = when (BuildConfig.DEBUG) {
                true -> 5000
                false -> calendar.timeInMillis - System.currentTimeMillis()
            }
            val request = OneTimeWorkRequest.Builder(ShowReminderWorker::class.java)
                    .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                    .setInputData(
                            Data.Builder()
                                    .put(Reminder.SHOW_ID, show.traktId)
                                    .put(Reminder.SHOW_TITLE, show.title)
                                    .build())
                    .build()
            val getNextEpisodeRequest = OneTimeWorkRequest.Builder(SyncNextEpisodeWorker::class.java)
                    .setInputData(Data.Builder()
                            .put(Reminder.SHOW_ID, show.traktId)
                            .build())
                    .build()
            workManager.beginWith(request)
                    .then(getNextEpisodeRequest)
                    .enqueue()

            val notification = SrNotification(null, showId, aheadOfTime, request.id.toString())
            notificationsRepository.createNotification(notification)
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
            val notification = notificationsRepository.getNotification(showId)
            notification?.let {
                workManager.cancelWorkById(UUID.fromString(it.workerId))
                notificationsRepository.deleteNotification(it)
            }
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
        scope.launch {
            val watchedEpisode = WatchedEpisode(null, episode.showId, episode.season, episode.number)
            watchedEpisodesRepository.setEpisodeWatched(watchedEpisode)
            val nextEpisodeAbsNumber = episode.absNumber + 1
            showsRepository.updateNextEpisode(episode.showId, nextEpisodeAbsNumber)

            val season = seasonsRepository.getSeason(episode.showId, episode.season)
            val nmbOfWatchedEpisodes = season.nmbOfWatchedEpisodes + 1
            if (nmbOfWatchedEpisodes < season.airedEpisodeCount) {
                seasonsRepository.updateSeason(season.copy(nmbOfWatchedEpisodes = nmbOfWatchedEpisodes))
            }
        }
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
}