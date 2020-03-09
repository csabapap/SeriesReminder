package hu.csabapap.seriesreminder.data.repositories.popularshows

import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import hu.csabapap.seriesreminder.utils.safeApiCall
import javax.inject.Inject

class RemotePopularDataSource @Inject constructor(val traktApi: TraktApi) {
    suspend fun popularShows(extended: String = "", page: Int = 1, limit: Int = 20) = safeApiCall(
            call = {traktApi.popularShows(extended, page, limit)},
            errorMessage = "fetch popular shows error")

}