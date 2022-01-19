package hu.csabapap.seriesreminder.data.repositories.trendingshows

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.data.db.TrendingShowsResult
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import hu.csabapap.seriesreminder.extensions.distinctUntilChanged
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrendingShowsRepository @Inject constructor(
        private val localTrendingDataSource: LocalTrendingDataSource,
        private val remoteTrendingDataSource: RemoteTrendingDataSourceImpl,
        private val showsRepository: ShowsRepository) {

    suspend fun getTrendingShows() = localTrendingDataSource.getShows(10)

    fun getTrendingShowsFlow() = localTrendingDataSource.getShowsFlow(10)

    fun getPaginatedTrendingShows(limit: Int = DATABASE_PAGE_SIZE): TrendingShowsResult {
        Timber.d("get trending shows")
        var page = 1
        val dataSourceFactory = localTrendingDataSource.getPaginatedShows(page, limit)
        val config = PagedList.Config.Builder()
                .setPageSize(DATABASE_PAGE_SIZE)
                .setInitialLoadSizeHint(1 * DATABASE_PAGE_SIZE)
                .build()
        val data: LiveData<PagedList<TrendingGridItem>> =
                LivePagedListBuilder(dataSourceFactory, config)
                        .setBoundaryCallback(object : PagedList.BoundaryCallback<TrendingGridItem>() {
                            override fun onItemAtEndLoaded(itemAtEnd: TrendingGridItem) {
                                page += 1
                                Timber.d("db page: $page")
                                GlobalScope.launch {
                                    updateTrendingShows()
                                }
                            }
                        })
                        .build().distinctUntilChanged()
        return TrendingShowsResult(data)
    }

    suspend fun refreshTrendingShow(): Result<List<SRTrendingItem>> = coroutineScope {
        localTrendingDataSource.clearTrendingShows()
        val trendingShowsResult = remoteTrendingDataSource.getDeferredPaginatedShows()

        if (trendingShowsResult is Result.Error) {
            return@coroutineScope Result.Error(trendingShowsResult.exception)
        }

        val trendingShows = if (trendingShowsResult is Result.Success) {
            trendingShowsResult.data
        } else {
            emptyList()
        }

        val trendingItems = trendingShows.map {
            async {
                val show = showsRepository.getShowWithImages(it.show.ids.trakt, it.show.ids.tvdb) ?: return@async null
                mapToSRTrendingShow(it.show.ids.trakt, it.watchers, 1)
            }
        }.awaitAll()
                .filterNotNull()
        localTrendingDataSource.insertShows(0, trendingItems)
        return@coroutineScope Result.Success(trendingItems)
    }

    suspend fun updateTrendingShows() {
        var lastPage = localTrendingDataSource.getLastPage()
        lastPage += 1
        if (lastPage > 3) return
        val trendingShowsResult = remoteTrendingDataSource.getDeferredPaginatedShows(lastPage, NETWORK_PAGE_SIZE)
        val trendingShows = if (trendingShowsResult is Result.Success) {
            trendingShowsResult.data
        } else {
            emptyList()
        }

        val srTrendingItems = trendingShows
                .map {
                    val traktId = it.show?.ids?.trakt ?: return@map null
                    val tvdbId = it.show?.ids?.tvdb ?: return@map null
                    showsRepository.getShowWithImages(traktId, tvdbId)
                    mapToSRTrendingShow(traktId, it.watchers, lastPage)
                }.filterNotNull()
        localTrendingDataSource.insertShows(lastPage, srTrendingItems)
    }

    private fun mapToSRTrendingShow(showId: Int, watchers: Int, page: Int) : SRTrendingItem =
            SRTrendingItem(null, showId, watchers, page)

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
        private const val NETWORK_PAGE_SIZE = 20
    }
}