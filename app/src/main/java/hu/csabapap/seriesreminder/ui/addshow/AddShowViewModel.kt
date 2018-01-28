package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import timber.log.Timber
import javax.inject.Inject

class AddShowViewModel @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val schedulers: AppRxSchedulers
) : ViewModel() {

    val showLiveData = MutableLiveData<AddShowState>()

    fun getShow(showId: Int) {
        showsRepository.getShow(traktId = showId)
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .subscribe({
                    showLiveData.value = AddShowState(it)
                },
                        { Timber.e(it)})
    }

}
