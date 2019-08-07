package hu.csabapap.seriesreminder.data.repositories.trendingshows

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.TrendingShowsResult
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import hu.csabapap.seriesreminder.extensions.distinctUntilChanged
import kotlinx.coroutines.*
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

    suspend fun refreshTrendingShow(): List<SRTrendingItem> = coroutineScope {
        val trendingShows = remoteTrendingDataSource.getShows("full").await()

        val trendingItems = trendingShows.map {
            async {
                showsRepository.getShowWithImages(it.show.ids.trakt, it.show.ids.tvdb).await()
                mapToSRTrendingShow(it.show.ids.trakt, it.watchers, 0)
            }
        }.awaitAll()
        localTrendingDataSource.insertShows(0, trendingItems)
        return@coroutineScope trendingItems
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