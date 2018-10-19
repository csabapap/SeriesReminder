package hu.csabapap.seriesreminder.data.repositories.trendingshows

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.TrendingShowsResult
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
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

    var updateTrendingShowsIsRunning = false

    fun getTrendingShows(limit: Int = DATABASE_PAGE_SIZE): TrendingShowsResult {
        Timber.d("get trending shows")
        val dataSourceFactory = localTrendingDataSource.getShows(limit)
        val data: LiveData<PagedList<TrendingGridItem>> =
                LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                        .setBoundaryCallback(object : PagedList.BoundaryCallback<TrendingGridItem>() {
                            var page  = 1

                            override fun onZeroItemsLoaded() {
                                Timber.d("on zero items loading")
                                if (updateTrendingShowsIsRunning.not()) {
                                    updateTrendingShows(1)
                                }
                            }

                            override fun onItemAtEndLoaded(itemAtEnd: TrendingGridItem) {
//                                if (updateTrendingShowsIsRunning.not()) {
//                                    page += 1
//                                    if (page < 4) {
//                                        updateTrendingShows(page)
//                                    }
//                                }
                            }
                        })
                        .build().distinctUntilChanged()
        return TrendingShowsResult(data)
    }

    fun updateTrendingShows(page: Int) {
        updateTrendingShowsIsRunning = true
        val start = System.currentTimeMillis()
        remoteTrendingDataSource.getPaginatedShows("full", page, NETWORK_PAGE_SIZE)
                .doOnSuccess{ Timber.d("get trending shows took %d ms", (System.currentTimeMillis() - start))}
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
                        updateTrendingShowsIsRunning = false
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                        updateTrendingShowsIsRunning = false
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