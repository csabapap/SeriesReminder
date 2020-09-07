package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.WatchedShow
import retrofit2.http.GET

interface UsersService {
    @GET("/users/me/watched/shows?extended=noseasons")
    suspend fun watchedShows(): List<WatchedShow>
}