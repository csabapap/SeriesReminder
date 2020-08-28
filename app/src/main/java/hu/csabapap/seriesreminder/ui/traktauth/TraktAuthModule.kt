package hu.csabapap.seriesreminder.ui.traktauth

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TraktAuthModule {
    @ContributesAndroidInjector
    abstract fun traktAuthFragment(): TraktAuthenticationFragment
}