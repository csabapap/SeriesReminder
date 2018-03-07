package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.EpisodeData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface TvdbEpisodeService {

    @GET("/episodes/{id}")
    fun episode(@Path("id") id: Int): Single<EpisodeData>

}