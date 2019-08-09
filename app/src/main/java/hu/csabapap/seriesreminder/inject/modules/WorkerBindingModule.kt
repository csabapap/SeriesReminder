package hu.csabapap.seriesreminder.inject.modules

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import hu.csabapap.seriesreminder.inject.WorkerKey
import hu.csabapap.seriesreminder.services.workers.ChildWorkerFactory
import hu.csabapap.seriesreminder.services.workers.ShowReminderWorker
import hu.csabapap.seriesreminder.services.workers.SyncNextEpisodeWorker
import hu.csabapap.seriesreminder.services.workers.SyncShowsWorker

@Module
interface WorkerBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(ShowReminderWorker::class)
    fun bindShowReminderWorker(factory: ShowReminderWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(SyncNextEpisodeWorker::class)
    fun bindSyncNextEpisodeWorker(factory: SyncNextEpisodeWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(SyncShowsWorker::class)
    fun bindSyncShowsWorker(factory: SyncShowsWorker.Factory): ChildWorkerFactory
}