package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Named

@Module
abstract class SearchModule {
    @ContributesAndroidInjector()
    internal abstract fun searchActivity() : SearchActivity

    @Binds
    @Named("SearchViewModelFactory")
    abstract fun providesSearchViewModelFactory(searchViewModelProvider: SearchViewModelProvider)
            : ViewModelProvider.Factory
}