package hu.csabapap.seriesreminder.services.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import hu.csabapap.seriesreminder.domain.SyncShowsUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class SyncShowsWorker(context: Context,
                      workerParameters: WorkerParameters,
                      private val syncShowsUseCase: SyncShowsUseCase)
    : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Timber.d("do work")
        return coroutineScope {
            val job = async {
                syncShowsUseCase.syncShows()
            }
            job.await()
            Result.success()
        }
    }

    class Factory @Inject constructor(private val syncShowsUseCase: SyncShowsUseCase)
        : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return SyncShowsWorker(appContext, params, syncShowsUseCase)
        }
    }
}