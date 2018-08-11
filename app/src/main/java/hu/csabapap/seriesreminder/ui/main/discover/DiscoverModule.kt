package hu.csabapap.seriesreminder.ui.main.discover

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import hu.csabapap.seriesreminder.inject.ViewModelKey

@Module
abstract class DiscoverModule {

    @ContributesAndroidInjector
    abstract fun gridFragment() : GridFragment

    @Binds
    @IntoMap
    @ViewModelKey(DiscoverViewModel::class)
    abstract fun providesViewModel(viewModel: DiscoverViewModel) : ViewModel

}