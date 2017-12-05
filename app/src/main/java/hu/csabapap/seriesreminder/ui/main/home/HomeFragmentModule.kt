package hu.csabapap.seriesreminder.ui.main.home

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideFragment() : HomeFragment

    @Binds
    abstract fun provideViewModel(viewModel: HomeViewModel) : ViewModel
}