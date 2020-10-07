package hu.csabapap.seriesreminder.data.repositories.trendingshows

import com.uwetrottmann.trakt5.entities.TrendingShow
import com.uwetrottmann.trakt5.enums.Extended
import com.uwetrottmann.trakt5.services.Shows
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.utils.safeApiCall
import java.io.IOException
import javax.inject.Inject

class RemoteTrendingDataSourceImpl @Inject constructor(private val showsServices: Shows) {

    suspend fun getDeferredPaginatedShows(page: Int = 1, limit: Int = 20) = safeApiCall({
        requestPaginatedTrendingShows(page = page, limit = limit)
    }, "fetch paginated trending shows error")

    private fun requestPaginatedTrendingShows(page: Int = 1, limit: Int = 15)
    : Result<List<TrendingShow>>{
        val response = showsServices.trending(page, limit, Extended.FULL).execute()
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