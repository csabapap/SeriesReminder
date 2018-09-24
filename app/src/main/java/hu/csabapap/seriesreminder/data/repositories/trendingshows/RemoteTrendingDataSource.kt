package hu.csabapap.seriesreminder.data.repositories.trendingshows

import hu.csabapap.seriesreminder.data.network.TraktApi
import javax.inject.Inject

class RemoteTrendingDataSource @Inject constructor(private val traktApi: TraktApi) {

    fun getShows(extended: String, page: Int = 1, limit: Int = 15) =
            traktApi.trendingShows(extended, page, limit)

}