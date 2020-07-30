package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.Episode
import retrofit2.http.GET
import retrofit2.http.Path

interface EpisodesService {

    @GET("shows/{id}/seasons/{season_number}/episodes/{episode_number}?extended=full")
    suspend fun episode(@Path("id") showId: Int,
                    @Path("season_number") seasonNumber: Int,
                    @Path("episode_number") episodeNumber: Int)
            : Episode
}