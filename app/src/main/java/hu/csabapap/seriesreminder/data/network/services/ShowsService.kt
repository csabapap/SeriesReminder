package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.NextEpisode
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ShowsService {

    @GET("shows/{id}/next_episode")
    fun nextEpisode(@Path("id") traktId: Int) : Deferred<Response<NextEpisode>>
}
