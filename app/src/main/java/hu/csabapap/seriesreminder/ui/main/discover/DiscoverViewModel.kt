package hu.csabapap.seriesreminder.ui.main.discover

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.GridItem
import hu.csabapap.seriesreminder.data.db.entities.Item
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(private val showsRepository: ShowsRepository)
    : ViewModel() {
    private val disposables = CompositeDisposable()
    val itemsLiveData = MutableLiveData<List<GridItem<Item>>>()

    fun getItems(type: Int) {
        when (type) {
            DiscoverFragment.TYPE_TRENDING -> getTrendingShows()
        }
    }

    private fun getTrendingShows() {
        disposables += showsRepository.getTrendingShows(20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    itemsLiveData.value = it as List<GridItem<Item>>
                }, { Timber.e(it) })
    }

    private fun getPopularShows() {

    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}