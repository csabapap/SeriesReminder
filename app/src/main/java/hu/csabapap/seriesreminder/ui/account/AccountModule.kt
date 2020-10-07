package hu.csabapap.seriesreminder.ui.account

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AccountModule {
    @ContributesAndroidInjector
    abstract fun accountFragment(): AccountFragment
}