package hu.csabapap.seriesreminder.data.repositories.shows

import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import hu.csabapap.seriesreminder.utils.safeApiCall
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val showsService: ShowsService) {

    suspend fun show(traktId: Int) = safeApiCall({
        val show = showsService.show(traktId)
        return@safeApiCall Result.Success(show)
    }, errorMessage = "get show error from trakt.tv")
}