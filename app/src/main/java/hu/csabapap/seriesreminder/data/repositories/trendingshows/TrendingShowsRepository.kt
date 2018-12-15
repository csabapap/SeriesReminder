package hu.csabapap.seriesreminder.data.repositories.trendingshows

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.TrendingShowsResult
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import hu.csabapap.seriesreminder.extensions.distinctUntilChanged
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
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
                                GlobalScope.launch {
                                    updateTrendingShows()
                                }
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

    suspend fun updateTrendingShows() {
        var lastPage = localTrendingDataSource.getLastPage()
        lastPage += 1
        if (lastPage > 3) return
        val trendingShows = remoteTrendingDataSource.getDeferredPaginatedShows("full", lastPage, NETWORK_PAGE_SIZE).await()
        val srTrendingItems = trendingShows
                .map {
                    showsRepository.getShowWithImages(it.show.ids.trakt, it.show.ids.tvdb).await()
                    mapToSRTrendingShow(it.show.ids.trakt, it.watchers, lastPage)
                }
        localTrendingDataSource.insertShows(lastPage, srTrendingItems)
    }

    private fun mapToSRTrendingShow(showId: Int, watchers: Int, page: Int) : SRTrendingItem =
            SRTrendingItem(null, showId, watchers, page)

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
        private const val NETWORK_PAGE_SIZE = 20
    }
}