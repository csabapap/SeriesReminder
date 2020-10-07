package hu.csabapap.seriesreminder.data.repositories.shows

import com.uwetrottmann.trakt5.enums.Extended
import com.uwetrottmann.trakt5.services.Shows
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.utils.safeApiCall
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val showsService: Shows
) {

    suspend fun show(traktId: Int) = safeApiCall({
        val show = showsService.summary(traktId.toString(), Extended.FULL).execute()
        return@safeApiCall Result.Success(show)
    }, errorMessage = "get show error from trakt.tv")

    fun showCall(traktId: Int) = showsService.summary(traktId.toString(), Extended.FULL)
}