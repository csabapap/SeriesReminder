package hu.csabapap.seriesreminder.inject.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import hu.csabapap.seriesreminder.services.SyncService

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun syncService(): SyncService

}