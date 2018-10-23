package hu.csabapap.seriesreminder.data.repositories.trendingshows

import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import javax.inject.Inject

class LocalTrendingDataSource @Inject constructor(private val trendingDao: TrendingDao) {

    fun insertShows(page: Int, trendingShows: List<SRTrendingItem>) {
        trendingDao.delete(page)
        trendingDao.insert(trendingShows)
    }

    fun getShows(limit: Int) = trendingDao.getTrendingShowsFactory(limit)

    fun getShowsFlowable(limit: Int) = trendingDao.getTrendingShows(limit)

    fun getLastPage() = trendingDao.getLastPage()

}