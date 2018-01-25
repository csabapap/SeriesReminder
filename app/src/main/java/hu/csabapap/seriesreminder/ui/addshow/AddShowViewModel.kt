package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import javax.inject.Inject

class AddShowViewModel @Inject constructor(
        val showsRepository: ShowsRepository,
        val schedulers: AppRxSchedulers
) : ViewModel() {

}
