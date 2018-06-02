package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Named

@Module
abstract class AddShowModule {

    @ContributesAndroidInjector(modules = [AddShowParamsModule::class])
    internal abstract fun addShowActivity() : AddShowActivity

    @Binds @Named("AddShowViewModelFactory")
    abstract fun providesAddShowViewModelFactory(addShowViewModelProvider: AddShowViewModelProvider)
            : ViewModelProvider.Factory
}