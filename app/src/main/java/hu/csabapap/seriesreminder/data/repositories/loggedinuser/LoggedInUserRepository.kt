package hu.csabapap.seriesreminder.data.repositories.loggedinuser

import android.content.SharedPreferences
import hu.csabapap.seriesreminder.data.models.LoggedInUser
import javax.inject.Inject

class LoggedInUserRepository @Inject constructor(private val preferences: SharedPreferences) {

    fun saveUser(accessToken: String, refreshToken: String) {
        preferences.edit()
                .putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .apply()
    }

    fun loggedInUser(): LoggedInUser? {
        val accessToken = preferences.getString("access_token", "") ?: ""
        val refreshToken = preferences.getString("refresh_token", "") ?: ""
        if (accessToken.isEmpty() || refreshToken.isEmpty()) {
            return null
        }
        return LoggedInUser(accessToken, refreshToken)
    }

    fun isLoggedIn() = loggedInUser() != null
}