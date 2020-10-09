package hu.csabapap.seriesreminder.data.repositories.popularshows

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.db.PopularShowsResult
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem
import hu.csabapap.seriesreminder.data.db.entities.SRPopularItem
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.extensions.distinctUntilChanged
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopularShowsRepository @Inject constructor(private val localPopularDataSource: LocalPopularDataSource,
                                                 private val remotePopularDataSource: RemotePopularDataSource,
                                                 private val showsRepository: ShowsRepository) {

    fun getPopularShowsFlow() = localPopularDataSource.getShowsFlow(10)

    fun getPopularShows(limit: Int = DATABASE_PAGE_SIZE): PopularShowsResult {
        Timber.d("get popular shows")
        val dataSourceFactory = localPopularDataSource.getShows(limit)
        val data: LiveData<PagedList<PopularGridItem>> =
                LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                        .setBoundaryCallback(object : PagedList.BoundaryCallback<PopularGridItem>() {
                            override fun onItemAtEndLoaded(itemAtEnd: PopularGridItem) {
                                GlobalScope.launch {
                                    updatePopularShows()
                                }
                            }
                        })
                        .build().distinctUntilChanged()
        return PopularShowsResult(data)
    }

    suspend fun refreshShows(): List<SRPopularItem> = coroutineScope {
        localPopularDataSource.clearShows()
        val result = remotePopularDataSource.popularShows("full")
        if (result is Result.Success) {
            val popularShows = result.data
            val popularItems = popularShows.map {
                async {
                    val show = showsRepository.getShowWithImages(it.ids.trakt, it.ids.tvdb)
                    if (show != null) {
                        return@async mapToSRPopularItem(show.traktId, 0)
                    } else {
                        return@async null
                    }

                }
            }
                    .awaitAll()
                    .filterNotNull()
            localPopularDataSource.insertShows(0, popularItems)
            return@coroutineScope popularItems
        }
        return@coroutineScope emptyList<SRPopularItem>()
    }

    suspend fun updatePopularShows() {
        val start = System.currentTimeMillis()
        var page = localPopularDataSource.getLastPage()
        page += 1
        if (page > 3) return
        val result = remotePopularDataSource.paginatedPopularShows("full", page, NETWORK_PAGE_SIZE)
        if (result is Result.Success) {
            val popularShows = result.data
            val popularShowItems = popularShows.map {
                showsRepository.getShowWithImages(it.ids.trakt, it.ids.tvdb) ?: return@map null
                mapToSRPopularItem(it.ids.trakt, page)
            }
                    .filterNotNull()
            localPopularDataSource.insertShows(page, popularShowItems)
            Timber.d("populars shows loaded in %d ms", (System.currentTimeMillis() - start))
        }
    }

    private fun mapToSRPopularItem(showId: Int, page: Int) : SRPopularItem =
            SRPopularItem(showId = showId, page = page)

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
        private const val NETWORK_PAGE_SIZE = 20
    }

}