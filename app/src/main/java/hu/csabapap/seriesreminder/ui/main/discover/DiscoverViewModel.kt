package hu.csabapap.seriesreminder.ui.main.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.PopularShowsResult
import hu.csabapap.seriesreminder.data.db.TrendingShowsResult
import hu.csabapap.seriesreminder.data.db.entities.GridItem
import hu.csabapap.seriesreminder.data.db.entities.Item
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val collectionRepository: CollectionRepository)
    : ViewModel() {
    private val disposables = CompositeDisposable()
    val itemsLiveData = MutableLiveData<List<GridItem<Item>>>()
    private val loadTrendingShows = MutableLiveData<Boolean>()
    private val loadPopularShows = MutableLiveData<Boolean>()
    val collectionLiveData = collectionRepository.getCollection()

    private val trendingShowsResult: LiveData<TrendingShowsResult> = Transformations.map(loadTrendingShows) {
        showsRepository.getTrendingShowsLiveData()
    }

    private val popularShowsResult: LiveData<PopularShowsResult> = Transformations.map(loadPopularShows) {
        showsRepository.getPopularShowsLiveData()
    }

    val trendingShows: LiveData<PagedList<TrendingGridItem>> = Transformations.switchMap(trendingShowsResult) {
        it -> it.data
    }

    val popularShows: LiveData<PagedList<PopularGridItem>> = Transformations.switchMap(popularShowsResult) {
        it -> it.data
    }

    fun getItems(type: Int) {
        when (type) {
            DiscoverFragment.TYPE_TRENDING -> loadTrendingShows()
            DiscoverFragment.TYPE_POPULAR -> loadPopularShows()
        }
    }

    fun loadTrendingShows() {
        loadTrendingShows.value = true
    }

    fun loadPopularShows() {
        loadPopularShows.value = true
    }

    private fun getTrendingShows() {
        disposables += showsRepository.getTrendingShows(20)
                .flatMap({ trendingItems ->
                    collectionRepository.getCollectionsSingle().toFlowable()
                            .flatMapIterable { it }
                            .map { it.entry?.showId }
                            .toList().toFlowable()
                            .flatMap{
                                for (item in trendingItems) {
                                    if (it.contains(item.show?.traktId)) {
                                        item.show?.inCollection = true
                                    }
                                }
                                Flowable.just(trendingItems)
                            }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    itemsLiveData.value = it as List<GridItem<Item>>
                }, { Timber.e(it) })
    }

    private fun getPopularShows() {
        disposables += showsRepository.popularShows(20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    itemsLiveData.value = it as List<GridItem<Item>>
                }, { Timber.e(it) })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}