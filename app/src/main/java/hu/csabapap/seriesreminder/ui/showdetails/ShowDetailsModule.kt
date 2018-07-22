package hu.csabapap.seriesreminder.ui.showdetails

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import hu.csabapap.seriesreminder.ui.addshow.AddShowActivity
import javax.inject.Named

@Module
abstract class ShowDetailsModule {

    @ContributesAndroidInjector()
    internal abstract fun addShowActivity() : AddShowActivity

    @Binds
    @Named("ShowDetailsViewModelFactory")
    abstract fun providesViewModelFactory(showDetailsViewModelProvider: ShowDetailsViewModelProvider)
            : ViewModelProvider.Factory

}