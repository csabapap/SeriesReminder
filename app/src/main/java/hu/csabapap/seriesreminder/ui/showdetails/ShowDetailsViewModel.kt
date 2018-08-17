package hu.csabapap.seriesreminder.ui.showdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ShowDetailsViewModel(private val showsRepository: ShowsRepository): ViewModel() {

    val showLiveData = MutableLiveData<SRShow>()

    fun getShow(showId: Int) {
        showsRepository.getShow(showId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.apply {
                        showLiveData.value = it
                    }
                }, Timber::e)

    }
}