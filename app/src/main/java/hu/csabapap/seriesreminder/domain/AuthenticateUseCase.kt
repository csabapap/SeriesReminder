package hu.csabapap.seriesreminder.domain

import com.uwetrottmann.trakt5.TraktV2
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.network.entities.TokenResponse
import hu.csabapap.seriesreminder.data.network.services.AuthService
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import hu.csabapap.seriesreminder.utils.safeApiCall
import javax.inject.Inject

class AuthenticateUseCase @Inject constructor(
        private val authService: AuthService,
        private val loggedInUserRepository: LoggedInUserRepository,
        val traktV2: TraktV2
) {

    suspend fun getToken(code: String, clientId: String, secretId: String, redirectUrl: String): Result<TokenResponse> {
        val result = safeApiCall({
            val response = authService.requestToken(code, clientId, secretId, redirectUrl)
            return@safeApiCall Result.Success(response)
        },
                "error during trakt authentication")
        if (result is Result.Success) {
            loggedInUserRepository.saveUser(result.data.accessToken, result.data.refreshToken)
            traktV2.accessToken(result.data.accessToken)
            traktV2.refreshToken(result.data.refreshToken)
        }
        return result
    }
}