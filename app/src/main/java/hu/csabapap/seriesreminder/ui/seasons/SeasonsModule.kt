package hu.csabapap.seriesreminder.ui.seasons

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Named

@Module
abstract class SeasonsModule {
    @ContributesAndroidInjector
    internal abstract fun seasonsActivity() : SeasonsActivity

    @Binds
    @Named("SeasonsViewModelFactory")
    abstract fun providesViewModelFactory(showDetailsViewModelProvider: SeasonsViewModelProvider)
            : ViewModelProvider.Factory
}