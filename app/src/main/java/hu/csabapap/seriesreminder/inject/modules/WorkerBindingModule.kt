package hu.csabapap.seriesreminder.inject.modules

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import hu.csabapap.seriesreminder.inject.WorkerKey
import hu.csabapap.seriesreminder.services.workers.ChildWorkerFactory
import hu.csabapap.seriesreminder.services.workers.ShowReminderWorker
import hu.csabapap.seriesreminder.services.workers.SyncNextEpisodeWorker

@Module
interface WorkerBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(ShowReminderWorker::class)
    fun bindShowReminderWorker(factory: ShowReminderWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(SyncNextEpisodeWorker::class)
    fun bindHelloWorldWorker(factory: SyncNextEpisodeWorker.Factory): ChildWorkerFactory
}