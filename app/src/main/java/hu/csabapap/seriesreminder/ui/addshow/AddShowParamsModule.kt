package hu.csabapap.seriesreminder.ui.addshow

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AddShowParamsModule {

    @Provides @Named("add_show_id")
    fun providesShowId(activity: AddShowActivity): Int {
        return activity.getShowId()
    }

}