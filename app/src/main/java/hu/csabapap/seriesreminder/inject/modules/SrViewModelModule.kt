package hu.csabapap.seriesreminder.inject.modules

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import hu.csabapap.seriesreminder.ui.main.MainViewModelProvider

@Module
abstract class SrViewModelModule {

    @Binds
    abstract fun provideViewModelFactory(mainViewModelProvider: MainViewModelProvider)
            : ViewModelProvider.Factory
}