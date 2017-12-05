package hu.csabapap.seriesreminder.inject.modules

import android.arch.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import hu.csabapap.seriesreminder.ui.main.MainViewModelProvider
import hu.csabapap.seriesreminder.ui.main.home.HomeViewModel
import javax.inject.Singleton

@Module
class SrViewModelModule {

    @Singleton
    @Provides
    fun provideViewModelFactory(homeViewModel: HomeViewModel) : ViewModelProvider.Factory {
        return MainViewModelProvider(homeViewModel)
    }

}