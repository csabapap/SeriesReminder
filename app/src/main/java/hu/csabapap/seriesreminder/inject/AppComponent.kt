package hu.csabapap.seriesreminder.inject

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.inject.modules.*
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.ui.addshow.AddShowModule
import hu.csabapap.seriesreminder.ui.search.SearchModule
import hu.csabapap.seriesreminder.ui.showdetails.ShowDetailsModule
import javax.inject.Singleton


@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            AppModule::class,
            ApiModule::class,
            DbModule::class,
            NetworkModule::class,
            HomeModule::class,
            AddShowModule::class,
            SearchModule::class,
            ServiceModule::class,
            ShowDetailsModule::class
        ])
interface AppComponent : AndroidInjector<DaggerApplication>{

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: SRApplication): Builder

        fun build(): AppComponent
    }
}