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
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopularShowsRepository @Inject constructor(private val localPopularDataSource: LocalPopularDataSource,
                                                 private val remotePopularDataSource: RemotePopularDataSource,
                                                 private val showsRepository: ShowsRepository) {

    fun getPopularShows(limit: Int = DATABASE_PAGE_SIZE): PopularShowsResult {
        Timber.d("get popular shows")
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
//                                page += 1
//                                if (page < 4) {
//                                    updatePopularShows(page)
//                                }
                            }
                        })
                        .build().distinctUntilChanged()
        return PopularShowsResult(data)
    }

    fun updatePopularShows(page: Int) {
        val start = System.currentTimeMillis()
        remotePopularDataSource.getShows("full", page, NETWORK_PAGE_SIZE)
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe { popularShow ->
                    val start = System.currentTimeMillis()
                    val ids = popularShow.ids
                    showsRepository.getShowWithImages(ids.trakt, ids.tvdb)
                            .map {
                                if (it.id != null) {
                                    Maybe.just(it)
                                } else {
                                    showsRepository.getShow(it.traktId)
                                }
                            }
                            .map { srShow ->
                                Timber.d("get show (%d) in %d ms", srShow.blockingGet().traktId, System.currentTimeMillis() - start)
                                mapToSRPopularItem(srShow.blockingGet().traktId, page)
                            }
                }
                .toList()
                .doOnSuccess { popularShows -> localPopularDataSource.insertShows(page, popularShows) }
                .subscribeOn(Schedulers.io())
                .subscribe({ Timber.d("populars shows loaded in %d ms", (System.currentTimeMillis() - start)) },
                        { Timber.e(it) })
    }

    private fun mapToSRPopularItem(showId: Int, page: Int) : SRPopularItem =
            SRPopularItem(showId = showId, page = page)

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
        private const val NETWORK_PAGE_SIZE = 20
    }

}