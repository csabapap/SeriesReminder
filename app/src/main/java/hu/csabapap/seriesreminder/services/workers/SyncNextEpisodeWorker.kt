package hu.csabapap.seriesreminder.services.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.domain.CreateNotificationAlarmUseCase
import hu.csabapap.seriesreminder.domain.GetNextEpisodeUseCase
import hu.csabapap.seriesreminder.utils.Reminder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncNextEpisodeWorker(context: Context,
                            workerParameters: WorkerParameters,
                            private val nextEpisodeUseCase: GetNextEpisodeUseCase,
                            private val showsRepository: ShowsRepository,
                            private val createNotificationAlarmUseCase: CreateNotificationAlarmUseCase)
    : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val showId = inputData.getInt(Reminder.SHOW_ID, -1)
        if (showId == -1) {
            return Result.failure()
        }

        GlobalScope.launch {
            val result = nextEpisodeUseCase.getNextEpisode(showId)
            if (result) {
                val show = showsRepository.getShow(showId)
                show?.let {
                    createAlarm(it)
                }
            }
        }

        return Result.success()
    }

    @SuppressLint("RestrictedApi")
    private suspend fun createAlarm(show: SRShow) {
        createNotificationAlarmUseCase.updateReminderAlarm(show.traktId)
    }

    class Factory @Inject constructor(private val nextEpisodeUseCase: GetNextEpisodeUseCase,
                                      private val showsRepository: ShowsRepository,
                                      private val createNotificationAlarmUseCase: CreateNotificationAlarmUseCase)
        : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return SyncNextEpisodeWorker(appContext, params, nextEpisodeUseCase, showsRepository,
                    createNotificationAlarmUseCase)
        }
    }
}