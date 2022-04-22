package hu.csabapap.seriesreminder.domain

import com.uwetrottmann.trakt5.TraktV2
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import hu.csabapap.seriesreminder.utils.safeApiCall
import retrofit2.HttpException
import javax.inject.Inject

class AuthenticateUseCase @Inject constructor(
        private val loggedInUserRepository: LoggedInUserRepository,
        val traktV2: TraktV2
) {

    suspend fun getToken(code: String): Result<UserData> {
        val result = safeApiCall({
            val response = traktV2.exchangeCodeForAccessToken(code)
            val tokens = response.body() ?: return@safeApiCall Result.Error(HttpException(response))
            val accessToken = tokens.access_token
                    ?: return@safeApiCall Result.Error(HttpException(response))
            val refreshToken = tokens.refresh_token
                    ?: return@safeApiCall Result.Error(HttpException(response))
            traktV2.accessToken(accessToken)
            traktV2.refreshToken(refreshToken)
            val userSettingsResponse = traktV2.users().settings().execute()
            val userSettings = if (userSettingsResponse.isSuccessful) {
                userSettingsResponse.body()
            } else {
                return@safeApiCall Result.Error(HttpException(response))
            }
            val username = userSettings?.user?.username
                    ?: return@safeApiCall Result.Error(HttpException(userSettingsResponse))
            val slug = userSettings.user?.ids?.slug
                    ?: return@safeApiCall Result.Error(HttpException(userSettingsResponse))
            val data = UserData(username, slug, accessToken, refreshToken)
            return@safeApiCall Result.Success(data)
        },
                "error during trakt authentication")
        if (result is Result.Success) {
            loggedInUserRepository.saveUser(
                    result.data.username,
                    result.data.slug,
                    result.data.accessToken,
                    result.data.refreshToken)
        }
        return result
    }
}

data class UserData(
        val username: String,
        val slug: String,
        val accessToken: String,
        val refreshToken: String)