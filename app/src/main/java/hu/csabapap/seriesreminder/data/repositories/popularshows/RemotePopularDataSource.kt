package hu.csabapap.seriesreminder.data.repositories.popularshows

import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.network.services.PopularShowsService
import hu.csabapap.seriesreminder.utils.safeApiCall
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class RemotePopularDataSource @Inject constructor(private val popularShowsService: PopularShowsService) {
    suspend fun popularShows(extended: String = "") = safeApiCall(
            call = {
                val response = popularShowsService.popularShows(extended)
                mapResponseToResult(response)
            },
            errorMessage = "fetch popular shows error")

    suspend fun paginatedPopularShows(extended: String = "", page: Int = 1, limit: Int = 20) = safeApiCall(
            call = {
                val response = popularShowsService.paginatedPopularShows(extended, page, limit)
                mapResponseToResult(response)
            },
            errorMessage = "fetch paginated popular shows error"
    )

    private fun mapResponseToResult(response: Response<List<Show>>): Result<List<Show>> {
        if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {
                return Result.Success(data)
            }
        }
        return Result.Error(
                IOException("Error getting popular shows ${response.code()} ${response.message()}")
        )
    }
}