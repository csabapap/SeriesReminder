package hu.csabapap.seriesreminder.domain

import com.uwetrottmann.trakt5.TraktV2
import com.uwetrottmann.trakt5.entities.AccessToken
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import hu.csabapap.seriesreminder.utils.safeApiCall
import retrofit2.HttpException
import javax.inject.Inject

class AuthenticateUseCase @Inject constructor(
        private val loggedInUserRepository: LoggedInUserRepository,
        val traktV2: TraktV2
) {

    suspend fun getToken(code: String): Result<AccessToken> {
        val result = safeApiCall({
            val response = traktV2.exchangeCodeForAccessToken(code)
            val result = response.body() ?: return@safeApiCall Result.Error(HttpException(response))
            return@safeApiCall Result.Success(result)
        },
                "error during trakt authentication")
        if (result is Result.Success) {
            loggedInUserRepository.saveUser(result.data.access_token, result.data.refresh_token)
            traktV2.accessToken(result.data.access_token)
            traktV2.refreshToken(result.data.refresh_token)
        }
        return result
    }
}