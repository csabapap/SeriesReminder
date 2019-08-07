package hu.csabapap.seriesreminder.ui.episode

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Named

@Module
abstract class EpisodeModule {

    @ContributesAndroidInjector
    internal abstract fun showDetailsActivity() : EpisodeActivity

    @Binds
    @Named("EpisodeViewModelFactory")
    abstract fun providesViewModelFactory(episodeViewModelProvider: EpisodeViewModelProvider)
            : ViewModelProvider.Factory
}