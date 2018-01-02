package hu.csabapap.seriesreminder.inject.modules

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.ui.main.MainViewModelProvider
import hu.csabapap.seriesreminder.ui.main.home.HomeViewModel
import javax.inject.Singleton

@Module
abstract class SrViewModelModule {

    @Binds
    abstract fun provideViewModelFactory(mainViewModelProvider: MainViewModelProvider) : ViewModelProvider.Factory

}