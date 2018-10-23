package hu.csabapap.seriesreminder.data.repositories.trendingshows

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.TrendingShowsResult
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import hu.csabapap.seriesreminder.extensions.distinctUntilChanged
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrendingShowsRepository @Inject constructor(private val localTrendingDataSource: LocalTrendingDataSource,
                              private val remoteTrendingDataSource: RemoteTrendingDataSource,
                              private val showsRepository: ShowsRepository) {

    fun getTrendingShowsFlowable() = localTrendingDataSource.getShowsFlowable(10)

    fun getTrendingShows(limit: Int = DATABASE_PAGE_SIZE): TrendingShowsResult {
        Timber.d("get trending shows")
        val dataSourceFactory = localTrendingDataSource.getShows(limit)
        val config = PagedList.Config.Builder()
                .setPageSize(DATABASE_PAGE_SIZE)
                .setInitialLoadSizeHint(1 * DATABASE_PAGE_SIZE)
                .build()
        val data: LiveData<PagedList<TrendingGridItem>> =
                LivePagedListBuilder(dataSourceFactory, config)
                        .setBoundaryCallback(object : PagedList.BoundaryCallback<TrendingGridItem>() {
                            override fun onItemAtEndLoaded(itemAtEnd: TrendingGridItem) {
                                var lastPage = localTrendingDataSource.getLastPage()
                                if (lastPage == null) {
                                    lastPage = 0
                                }
                                updateTrendingShows(lastPage + 1)
                            }
                        })
                        .build().distinctUntilChanged()
        return TrendingShowsResult(data)
    }

    fun refreshTrendingShows(): Single<List<SRTrendingItem>> {
        return remoteTrendingDataSource.getShows("full")
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe { trendingShow ->
                    val ids = trendingShow.show.ids
                    showsRepository.getShowWithImages(ids.trakt, ids.tvdb)
                            .map {
                                if (it.id != null) {
                                    Maybe.just(it)
                                } else {
                                    showsRepository.getShow(it.traktId)
                                }
                            }
                            .map { srShow -> mapToSRTrendingShow(srShow.blockingGet().traktId,
                                    trendingShow.watchers, 0) }
                }
                .toList()
                .doOnSuccess { trendingShows -> localTrendingDataSource.insertShows(0, trendingShows)}
    }

    fun updateTrendingShows(page: Int) {
        remoteTrendingDataSource.getPaginatedShows("full", page, NETWORK_PAGE_SIZE)
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe { trendingShow ->
                    val ids = trendingShow.show.ids
                    showsRepository.getShowWithImages(ids.trakt, ids.tvdb)
                            .map {
                                if (it.id != null) {
                                    Maybe.just(it)
                                } else {
                                    showsRepository.getShow(it.traktId)
                                }
                            }
                            .map { srShow -> mapToSRTrendingShow(srShow.blockingGet().traktId,
                                    trendingShow.watchers, page) }
                }
                .toList()
                .doOnSuccess { trendingShows -> localTrendingDataSource.insertShows(page, trendingShows)}
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<List<SRTrendingItem>> {
                    override fun onSuccess(t: List<SRTrendingItem>) {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                    }

                })
    }

    private fun mapToSRTrendingShow(showId: Int, watchers: Int, page: Int) : SRTrendingItem =
            SRTrendingItem(null, showId, watchers, page)

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
        private const val NETWORK_PAGE_SIZE = 20
    }
}