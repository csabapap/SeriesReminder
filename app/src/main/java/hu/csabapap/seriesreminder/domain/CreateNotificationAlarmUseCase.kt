package hu.csabapap.seriesreminder.domain

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.SrNotification
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.notifications.NotificationsRepository
import hu.csabapap.seriesreminder.services.workers.ShowReminderWorker
import hu.csabapap.seriesreminder.services.workers.SyncNextEpisodeWorker
import hu.csabapap.seriesreminder.utils.Reminder
import hu.csabapap.seriesreminder.utils.getAirDateTimeInCurrentTimeZone
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CreateNotificationAlarmUseCase @Inject constructor(
        private val notificationsRepository: NotificationsRepository,
        private val showsRepository: ShowsRepository,
        private val episodesRepository: EpisodesRepository,
        private val workManager: WorkManager
) {
    suspend fun createReminderAlarm(showId: Int, aheadOfTime: Int) {
        val show = showsRepository.getShow(showId).blockingGet() ?: return
        val upcomingEpisode = episodesRepository.getUpcomingEpisode(showId) ?: return
        val requestId = createAlarm(show, upcomingEpisode.episode.absNumber, aheadOfTime) ?: return
        val notification = SrNotification(null, showId, aheadOfTime, requestId)
        notificationsRepository.createNotification(notification)
    }

    suspend fun updateReminderAlarm(showId: Int) {
        val notification = notificationsRepository.getNotification(showId) ?: return
        val show = showsRepository.getShow(showId).blockingGet() ?: return
        val upcomingEpisode = episodesRepository.getUpcomingEpisode(showId) ?: return
        val requestId = createAlarm(show, upcomingEpisode.episode.absNumber, notification.delay) ?: return
        val updatedNotification = notification.copy(workerId = requestId)
        notificationsRepository.update(updatedNotification)
        Timber.d("notification alert created")
    }

    fun cancelNotification(showId: Int) {
        val notification = notificationsRepository.getNotification(showId)
        notification?.let {
            workManager.cancelWorkById(UUID.fromString(it.workerId))
            notificationsRepository.deleteNotification(it)
        }
    }

    private fun createAlarm(show: SRShow, episodeNumber: Int, aheadOfTime: Int): String? {
        val airDateTime = getAirDateTimeInCurrentTimeZone(LocalDateTime.now(), show.airingTime)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, airDateTime.dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, airDateTime.hour)
        calendar.set(Calendar.MINUTE, airDateTime.minute)
        calendar.set(Calendar.SECOND, 0)
        val duration = calendar.timeInMillis - System.currentTimeMillis() - aheadOfTime
        Timber.d("duration: ${duration}ms")
        if (duration < 0) {
            return null
        }
        val request = OneTimeWorkRequest.Builder(ShowReminderWorker::class.java)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                .setInputData(
                        Data.Builder()
                                .put(Reminder.SHOW_ID, show.traktId)
                                .put(Reminder.SHOW_TITLE, show.title)
                                .put(Reminder.EPISODE_ABS_NUMBER, episodeNumber)
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

        return request.id.toString()
    }
}