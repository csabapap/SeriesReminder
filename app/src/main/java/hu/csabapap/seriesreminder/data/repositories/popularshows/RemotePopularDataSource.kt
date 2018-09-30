package hu.csabapap.seriesreminder.data.repositories.popularshows

import hu.csabapap.seriesreminder.data.network.TraktApi
import javax.inject.Inject

class RemotePopularDataSource @Inject constructor(val traktApi: TraktApi) {

    fun getShows(extended: String, page: Int = 1, limit: Int = 15) =
            traktApi.popularShows(extended, page, limit)

}