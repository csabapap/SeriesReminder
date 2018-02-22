package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.Episode
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EpisodesService {

    @GET("shows/{id}/season/{season_number}/episodes/{episode_number}")
    fun episode(@Path("id") showId: Int,
                    @Path("season_number") seasonNumber: Int,
                    @Path("episode_number") episodeNumber: Int)
            : Single<Response<Episode>>
}