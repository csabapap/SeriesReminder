package hu.csabapap.seriesreminder.ui.main.home

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import hu.csabapap.seriesreminder.inject.ViewModelKey

@Module
abstract class HomeFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideFragment() : HomeFragment

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun provideViewModel(viewModel: HomeViewModel) : ViewModel
}