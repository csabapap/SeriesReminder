package hu.csabapap.seriesreminder.data.repositories.trendingshows

import hu.csabapap.seriesreminder.data.network.TraktApi
import javax.inject.Inject

class RemoteTrendingDataSource @Inject constructor(private val traktApi: TraktApi) {

    fun getPaginatedShows(extended: String, page: Int = 1, limit: Int = 15) =
            traktApi.paginatedTrendingShows(extended, page, limit)

    fun getShows(extended: String) =
            traktApi.trendingShows(extended)

}