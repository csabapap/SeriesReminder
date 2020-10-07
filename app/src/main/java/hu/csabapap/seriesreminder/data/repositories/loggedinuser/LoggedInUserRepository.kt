package hu.csabapap.seriesreminder.data.repositories.loggedinuser

import android.content.SharedPreferences
import hu.csabapap.seriesreminder.data.models.LoggedInUser
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
class LoggedInUserRepository @Inject constructor(private val preferences: SharedPreferences) {

    var onUserStateChanged: ((Boolean) -> Unit)? = null

    var loggedIn: Boolean by Delegates.observable(false, { _, oldValue, newValue ->
        if (oldValue != newValue) {
            onUserStateChanged?.invoke(newValue)
        }
    })

    fun saveUser(accessToken: String, refreshToken: String) {
        preferences.edit()
                .putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .apply()
        loggedIn = true
    }

    fun loggedInUser(): LoggedInUser? {
        val accessToken = preferences.getString("access_token", "") ?: ""
        val refreshToken = preferences.getString("refresh_token", "") ?: ""
        if (accessToken.isEmpty() || refreshToken.isEmpty()) {
            loggedIn = false
            return null
        }
        loggedIn = true
        return LoggedInUser(accessToken, refreshToken)
    }

    fun isLoggedIn() = loggedInUser() != null

    fun logout() {
        preferences.edit()
                .remove("access_token")
                .remove("refresh_token")
                .apply()
        loggedIn = false
    }
}