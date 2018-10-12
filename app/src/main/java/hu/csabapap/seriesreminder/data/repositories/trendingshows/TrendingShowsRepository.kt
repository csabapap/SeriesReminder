package hu.csabapap.seriesreminder.data.repositories.trendingshows

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.TrendingShowsResult
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
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

    fun getTrendingShows(limit: Int = DATABASE_PAGE_SIZE): TrendingShowsResult {
        val dataSourceFactory = localTrendingDataSource.getShows(limit)
        val data: LiveData<PagedList<TrendingGridItem>> =
                LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                        .setBoundaryCallback(object : PagedList.BoundaryCallback<TrendingGridItem>() {
                            var page  = 1

                            override fun onZeroItemsLoaded() {
                                updateTrendingShows(1)
                            }

                            override fun onItemAtEndLoaded(itemAtEnd: TrendingGridItem) {
                                page += 1
                                if (page < 4) {
                                    updateTrendingShows(page)
                                }
                            }
                        })
                        .build()
        return TrendingShowsResult(data)
    }

    fun updateTrendingShows(page: Int) {
        remoteTrendingDataSource.getShows("full", page, NETWORK_PAGE_SIZE)
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe { trendingShow ->
                    showsRepository.getShow(trendingShow.show.ids.trakt)
                            .map { showsRepository.insertOrUpdateShow(it) }
                            .map { showsRepository.getShow(it.traktId)}
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