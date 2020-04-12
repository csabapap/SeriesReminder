package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import hu.csabapap.seriesreminder.inject.ViewModelKey
import hu.csabapap.seriesreminder.ui.main.discover.DiscoverViewModel
import hu.csabapap.seriesreminder.ui.main.discover.GridFragment
import javax.inject.Named

@Module
abstract class SearchModule {
    @ContributesAndroidInjector()
    internal abstract fun searchFragment() : SearchFragment

    @ContributesAndroidInjector
    abstract fun gridFragment() : GridFragment

    @Binds
    @Named("SearchViewModelFactory")
    abstract fun providesSearchViewModelFactory(searchViewModelProvider: SearchViewModelProvider)
            : ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(DiscoverViewModel::class)
    abstract fun providesViewModel(viewModel: DiscoverViewModel) : ViewModel
}