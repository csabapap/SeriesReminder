package hu.csabapap.seriesreminder.inject

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.inject.modules.*
import hu.csabapap.seriesreminder.ui.addshow.AddShowModule
import javax.inject.Singleton


@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            AppModule::class,
            ActivityBindingModule::class,
            ApiModule::class,
            SrViewModelModule::class,
            HomeModule::class,
            AddShowModule::class])
interface AppComponent : AndroidInjector<DaggerApplication>{

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: SRApplication): Builder

        fun build(): AppComponent
    }
}