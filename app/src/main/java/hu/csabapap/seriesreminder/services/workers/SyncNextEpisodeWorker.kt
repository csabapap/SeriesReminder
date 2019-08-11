package hu.csabapap.seriesreminder.services.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.*
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.repositories.nextepisodes.NextEpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.notifications.NotificationsRepository
import hu.csabapap.seriesreminder.data.states.NextEpisodeSuccess
import hu.csabapap.seriesreminder.utils.Reminder
import hu.csabapap.seriesreminder.utils.getDateTimeForNextAir
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncNextEpisodeWorker(context: Context,
                            workerParameters: WorkerParameters,
                            private val nextEpisodesRepository: NextEpisodesRepository,
                            private val showsRepository: ShowsRepository,
                            private val notificationsRepository: NotificationsRepository,
                            private val workManager: WorkManager)
    : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val showId = inputData.getInt(Reminder.SHOW_ID, -1)
        if (showId == -1) {
            return Result.failure()
        }

        GlobalScope.launch {
            val state = nextEpisodesRepository.fetchAndSaveNextEpisode(showId)
            if (state is NextEpisodeSuccess) {
                val show = showsRepository.getShow(showId).await()
                show?.let {
                    createAlarm(it)
                }
            }
        }

        return Result.success()
    }

    @SuppressLint("RestrictedApi")
    private fun createAlarm(show: SRShow) {
        val day = show.airingTime.day
        val hours = show.airingTime.time
        val airDateTime = getDateTimeForNextAir(OffsetDateTime.now(ZoneOffset.UTC), day, hours)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, airDateTime.dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, airDateTime.hour)
        calendar.set(Calendar.MINUTE, airDateTime.minute)
        calendar.set(Calendar.SECOND, 0)
        val duration = when(BuildConfig.DEBUG) {
            true -> 1 * 60 * 1000
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

        val notification = notificationsRepository.getNotification(show.traktId)
        notification?.let {
            val updatedNotification = it.copy(workerId = request.id.toString())
            notificationsRepository.update(updatedNotification)
        }
    }

    class Factory @Inject constructor(private val nextEpisodesRepository: NextEpisodesRepository,
                                      private val showsRepository: ShowsRepository,
                                      private val notificationsRepository: NotificationsRepository,
                                      private val workManager: WorkManager)
        : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return SyncNextEpisodeWorker(appContext, params, nextEpisodesRepository,
                    showsRepository, notificationsRepository, workManager)
        }
    }
}