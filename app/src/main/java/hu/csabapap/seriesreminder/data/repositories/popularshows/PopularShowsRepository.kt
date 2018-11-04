package hu.csabapap.seriesreminder.data.repositories.popularshows

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.PopularShowsResult
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem
import hu.csabapap.seriesreminder.data.db.entities.SRPopularItem
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
class PopularShowsRepository @Inject constructor(private val localPopularDataSource: LocalPopularDataSource,
                                                 private val remotePopularDataSource: RemotePopularDataSource,
                                                 private val showsRepository: ShowsRepository) {

    fun getPopularShowsFlowable() = localPopularDataSource.getShowsFlowable(10)

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

    fun refreshPopularShows(): Single<List<SRPopularItem>> {
        return remotePopularDataSource.getShows("full")
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe { popularShow ->
                    val ids = popularShow.ids
                    showsRepository.getShowWithImages(ids.trakt, ids.tvdb)
                            .map {
                                if (it.id != null) {
                                    Maybe.just(it)
                                } else {
                                    showsRepository.getShow(it.traktId)
                                }
                            }
                            .map { srShow -> mapToSRPopularItem(srShow.blockingGet().traktId, 0) }
                }
                .toList()
                .doOnSuccess { popularShows -> localPopularDataSource.insertShows(0, popularShows)}
    }

    suspend fun updatePopularShows() {
        val start = System.currentTimeMillis()
        var page = localPopularDataSource.getLastPage()
        page += 1
        val popularShows = remotePopularDataSource.getShows("full", page, NETWORK_PAGE_SIZE).await()
        val popularShowItems = popularShows.map {
            showsRepository.getShowWithImages(it.ids.trakt, it.ids.tvdb).await()
            mapToSRPopularItem(it.ids.trakt, page)
        }
        localPopularDataSource.insertShows(page, popularShowItems)
        Timber.d("populars shows loaded in %d ms", (System.currentTimeMillis() - start))
    }

    private fun mapToSRPopularItem(showId: Int, page: Int) : SRPopularItem =
            SRPopularItem(showId = showId, page = page)

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
        private const val NETWORK_PAGE_SIZE = 20
    }

}