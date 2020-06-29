    package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.Season
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface SeasonsService {
    @GET("shows/{id}/seasons?extended=full,episodes")
    suspend fun seasons(@Path("id") showId: Int): List<Season>
}