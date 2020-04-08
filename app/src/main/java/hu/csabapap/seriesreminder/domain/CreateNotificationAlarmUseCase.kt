package hu.csabapap.seriesreminder.domain

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.daos.NotificationsDao
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
        private val workManager: WorkManager
) {

    operator fun invoke(showId: Int) {
        val notification = notificationsRepository.getNotification(showId) ?: return

        val show = showsRepository.getShow(showId).blockingGet() ?: return

        val airDateTime = getAirDateTimeInCurrentTimeZone(LocalDateTime.now(), show.airingTime)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, airDateTime.dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, airDateTime.hour)
        calendar.set(Calendar.MINUTE, airDateTime.minute)
        calendar.set(Calendar.SECOND, 0)
        val duration = calendar.timeInMillis - System.currentTimeMillis() - notification.delay
        Timber.d("duration: ${duration}ms")
        if (duration < 0) {
            return
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

        val updatedNotification = notification.copy(workerId = request.id.toString())
        notificationsRepository.update(updatedNotification)
        Timber.d("notification alert created")
    }

}