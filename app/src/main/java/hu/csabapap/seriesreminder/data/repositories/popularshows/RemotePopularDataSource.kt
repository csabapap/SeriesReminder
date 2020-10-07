package hu.csabapap.seriesreminder.data.repositories.popularshows

import com.uwetrottmann.trakt5.entities.Show
import com.uwetrottmann.trakt5.enums.Extended
import com.uwetrottmann.trakt5.services.Shows
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.utils.safeApiCall
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class RemotePopularDataSource @Inject constructor(private val shows: Shows) {
    suspend fun popularShows(extended: String = "") = safeApiCall(
            call = {
                val response = shows.popular(1, 20, Extended.FULL).execute()
                mapResponseToResult(response)
            },
            errorMessage = "fetch popular shows error")

    suspend fun paginatedPopularShows(extended: String = "", page: Int = 1, limit: Int = 20) = safeApiCall(
            call = {
                val response = shows.popular(page, limit, Extended.FULL).execute()
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