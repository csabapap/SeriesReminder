package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import hu.csabapap.seriesreminder.inject.ViewModelKey

@Module
abstract class AddShowModule {

    @ContributesAndroidInjector
    internal abstract fun addShowActivity() : AddShowActivity

    @Binds
    @IntoMap
    @ViewModelKey(AddShowViewModel::class)
    abstract fun providesViewModel(viewModel: AddShowViewModel) : ViewModel
}