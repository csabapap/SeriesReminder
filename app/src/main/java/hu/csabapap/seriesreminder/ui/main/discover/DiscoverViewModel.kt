package hu.csabapap.seriesreminder.ui.main.discover

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.GridItem
import hu.csabapap.seriesreminder.data.db.entities.Item
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(val showsRepository: ShowsRepository) : ViewModel() {

    val itemsLiveData = MutableLiveData<List<GridItem<Item>>>()

    fun getItems(type: Int) {
        when (type) {
            DiscoverFragment.TYPE_TRENDING -> getTrendingShows()
        }
    }

    private fun getTrendingShows() {
        showsRepository.getTrendingShows()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    itemsLiveData.value = it as List<GridItem<Item>>
                }
    }

    fun getPopularShows() {

    }
}