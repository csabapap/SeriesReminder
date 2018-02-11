package hu.csabapap.seriesreminder.ui.main.collection

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import hu.csabapap.seriesreminder.inject.ViewModelKey

@Module
abstract class CollectionModule {
    @ContributesAndroidInjector
    abstract fun collectionFragment() : CollectionFragment

    @Binds
    @IntoMap
    @ViewModelKey(CollectionViewModel::class)
    abstract fun providesViewModel(viewModel: CollectionViewModel) : ViewModel
}