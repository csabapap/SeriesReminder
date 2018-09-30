package hu.csabapap.seriesreminder.data.repositories.popularshows

import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.entities.SRPopularItem
import javax.inject.Inject

class LocalPopularDataSource @Inject constructor(private val popularDao: PopularDao) {

    fun getShows(limit: Int) = popularDao.getPopularShowsLiveFactory(limit)

    fun insertShows(page: Int, trendingShows: List<SRPopularItem>) {
        popularDao.delete(page)
        popularDao.insert(trendingShows)
    }
}