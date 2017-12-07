package hu.csabapap.seriesreminder.inject.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import hu.csabapap.seriesreminder.inject.ActivityScope
import hu.csabapap.seriesreminder.ui.add_show.AddShowActivity


@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector()
    internal abstract fun addShowActivity(): AddShowActivity
}