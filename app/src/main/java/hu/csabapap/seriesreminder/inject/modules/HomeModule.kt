package hu.csabapap.seriesreminder.inject.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import hu.csabapap.seriesreminder.ui.main.HomeActivity
import hu.csabapap.seriesreminder.ui.main.MainViewModelProvider
import hu.csabapap.seriesreminder.ui.main.collection.CollectionModule
import hu.csabapap.seriesreminder.ui.main.discover.DiscoverModule
import hu.csabapap.seriesreminder.ui.main.home.HomeFragmentModule
import javax.inject.Named

@Module
abstract class HomeModule {

    @ContributesAndroidInjector(modules = [
        HomeFragmentModule::class,
        DiscoverModule::class,
        CollectionModule::class
    ])
    internal abstract fun homeActivity(): HomeActivity

    @Binds
    @Named("Main")
    abstract fun provideViewModelFactory(mainViewModelProvider: MainViewModelProvider)
            : ViewModelProvider.Factory

}