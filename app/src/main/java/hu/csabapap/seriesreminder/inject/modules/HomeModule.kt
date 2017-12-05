package hu.csabapap.seriesreminder.inject.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import hu.csabapap.seriesreminder.ui.main.HomeActivity
import hu.csabapap.seriesreminder.ui.main.home.HomeFragmentModule

@Module
abstract class HomeModule {

    @ContributesAndroidInjector(modules = [
        HomeFragmentModule::class
    ])
    internal abstract fun homeActivity(): HomeActivity

}