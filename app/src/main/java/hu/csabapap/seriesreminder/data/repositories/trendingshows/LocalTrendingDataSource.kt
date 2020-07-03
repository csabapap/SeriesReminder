package hu.csabapap.seriesreminder.data.repositories.trendingshows

import androidx.paging.DataSource
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.db.entities.SRTrendingItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import javax.inject.Inject

class LocalTrendingDataSource @Inject constructor(private val trendingDao: TrendingDao) {

    fun insertShows(page: Int, trendingShows: List<SRTrendingItem>) {
        trendingDao.delete(page)
        trendingDao.insert(trendingShows)
    }

    fun getPaginatedShows(page: Int, limit: Int): DataSource.Factory<Int, TrendingGridItem> {
        return trendingDao.getTrendingShowsFactory(limit)
    }

    suspend fun getShows(limit: Int) = trendingDao.getTrendingShows(limit)

    fun getShowsFlow(limit: Int) = trendingDao.getTrendingShowsFlow(limit)

    fun clearTrendingShows() = trendingDao.deleteAll()

    fun getLastPage(): Int {
        return trendingDao.getLastPage() ?: 0
    }

}