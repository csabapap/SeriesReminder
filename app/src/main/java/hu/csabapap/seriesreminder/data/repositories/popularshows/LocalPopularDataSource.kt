package hu.csabapap.seriesreminder.data.repositories.popularshows

import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.entities.SRPopularItem
import javax.inject.Inject

class LocalPopularDataSource @Inject constructor(private val popularDao: PopularDao) {

    fun getShows(limit: Int) = popularDao.getPopularShowsLiveFactory(limit)

    fun getShowsFlow(limit: Int) = popularDao.getPopularShowsFlow(limit)

    fun insertShows(page: Int, popularShows: List<SRPopularItem>) {
        popularDao.delete(page)
        popularDao.insert(popularShows)
    }

    fun getLastPage() = popularDao.getLastPage() ?: 0

    fun clearShows() = popularDao.deleteAll()
}