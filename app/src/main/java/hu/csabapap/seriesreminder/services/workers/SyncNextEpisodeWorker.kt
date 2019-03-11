package hu.csabapap.seriesreminder.services.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.Reminder
import timber.log.Timber
import javax.inject.Inject

class SyncNextEpisodeWorker(context: Context,
                            workerParameters: WorkerParameters,
                            val showsRepository: ShowsRepository)
    : Worker(context, workerParameters) {

    override fun doWork(): Result {
        Timber.d("do work")
        Timber.d("shows repository is null? %b", showsRepository == null)
        val showId = inputData.getInt(Reminder.SHOW_ID, -1)
        if (showId == -1) {
            return Result.failure()
        }


        return Result.success()
    }

    class Factory @Inject constructor(val showsRepository: ShowsRepository): ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return SyncNextEpisodeWorker(appContext, params, showsRepository)
        }
    }
}