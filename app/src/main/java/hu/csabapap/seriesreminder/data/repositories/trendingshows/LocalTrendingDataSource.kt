package hu.csabapap.seriesreminder.data.repositories.trendingshows

import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import javax.inject.Inject

class LocalTrendingDataSource @Inject constructor(private val trendingDao: TrendingDao) {

    fun insertShows(trendingShows: List<SRTrendingItem>) {
        trendingDao.insert(trendingShows)
    }

    fun getShows() = trendingDao.getTrendingShowsFactory()

}