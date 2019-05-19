package hu.csabapap.seriesreminder.ui.showdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SrNotification
import hu.csabapap.seriesreminder.data.repositories.notifications.NotificationsRepository
import hu.csabapap.seriesreminder.data.repositories.relatedshows.RelatedShowsRepository
import hu.csabapap.seriesreminder.services.workers.ShowReminderWorker
import hu.csabapap.seriesreminder.services.workers.SyncNextEpisodeWorker
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.Reminder
import hu.csabapap.seriesreminder.utils.getDateTimeForNextAir
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class ShowDetailsViewModel(private val showsRepository: ShowsRepository,
                           private val collectionRepository: CollectionRepository,
                           private val notificationsRepository: NotificationsRepository,
                           private val relatedShowsRepository: RelatedShowsRepository,
                           private val workManager: WorkManager,
                           private val dispatchers: AppCoroutineDispatchers): ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _detailsUiState = MutableLiveData<ShowDetailsState>()
    val detailsUiState: LiveData<ShowDetailsState>
        get() = _detailsUiState

    fun getShow(showId: Int) {
        showsRepository.getShow(showId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.apply {
                        _detailsUiState.value = ShowDetailsState.Show(this)
                    }
                }, Timber::e)
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
            val duration = when(BuildConfig.DEBUG) {
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
                    true ->  ShowDetailsState.Notification(notification)
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
            relatedShowsRepository.refreshRelatedShows(id)
        }
    }
}