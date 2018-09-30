package hu.csabapap.seriesreminder.data.repositories.popularshows

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.PopularShowsResult
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem
import hu.csabapap.seriesreminder.data.db.entities.SRPopularItem
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopularShowsRepository @Inject constructor(private val localPopularDataSource: LocalPopularDataSource,
                                                 private val remotePopularDataSource: RemotePopularDataSource,
                                                 private val showsRepository: ShowsRepository) {

    fun getPopularShows(limit: Int = DATABASE_PAGE_SIZE): PopularShowsResult {
        Timber.d("get trending shows")
        // todo filter null items
        val dataSourceFactory = localPopularDataSource.getShows(limit)
        val data: LiveData<PagedList<PopularGridItem>> =
                LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                        .setBoundaryCallback(object : PagedList.BoundaryCallback<PopularGridItem>() {
                            var page  = 1

                            override fun onZeroItemsLoaded() {
                                updatePopularShows(1)
                            }

                            override fun onItemAtEndLoaded(itemAtEnd: PopularGridItem) {
                                page += 1
                                if (page < 4) {
                                    updatePopularShows(page)
                                }
                            }
                        })
                        .build()
        return PopularShowsResult(data)
    }

    fun updatePopularShows(page: Int) {
        remotePopularDataSource.getShows("full", page, NETWORK_PAGE_SIZE)
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe { popularShow ->
                    showsRepository.getShow(popularShow.ids.trakt)
                            .map { showsRepository.insertOrUpdateShow(it) }
                            .map { showsRepository.getShow(it.traktId) }
                            .map { srShow ->
                                mapToSRPopularItem(srShow.blockingGet().traktId, page)
                            }
                }
                .toList()
                .doOnSuccess { trendingShows -> localPopularDataSource.insertShows(page, trendingShows) }
                .subscribeOn(Schedulers.io())
                .subscribe({ Timber.d("nmb of shows loaded: %d", it.size) },
                        { Timber.e(it) })
    }

    private fun mapToSRPopularItem(showId: Int, page: Int) : SRPopularItem =
            SRPopularItem(showId = showId, page = page)

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
        private const val NETWORK_PAGE_SIZE = 20
    }

}