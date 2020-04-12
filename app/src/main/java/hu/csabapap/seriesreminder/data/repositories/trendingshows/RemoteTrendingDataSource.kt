package hu.csabapap.seriesreminder.data.repositories.trendingshows

import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import hu.csabapap.seriesreminder.data.network.services.TrendingShowsService
import hu.csabapap.seriesreminder.utils.safeApiCall
import java.io.IOException
import javax.inject.Inject

class RemoteTrendingDataSource @Inject constructor(private val trendingService: TrendingShowsService) {

    suspend fun getDeferredPaginatedShows(page: Int = 1, limit: Int = 20) = safeApiCall({
        requestPaginatedTrendingShows(extended = "full", page = page, limit = limit)
    }, "fetch paginated trending shows error")

    private suspend fun requestPaginatedTrendingShows(extended: String, page: Int = 1, limit: Int = 15)
    : Result<List<TrendingShow>>{
        val response = trendingService.paginatedTrendingShows(extended, page, limit)
        if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {
                return Result.Success(data)
            }
        }

        return Result.Error(
                IOException("Error getting paginated trending shows ${response.code()} ${response.message()}")
        )
    }
}