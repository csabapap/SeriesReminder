package hu.csabapap.seriesreminder.data.models

data class LoggedInUser(
        val username: String,
        val slug: String,
        val accessToken: String,
        val refreshToken: String)