package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.EpisodeData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TvdbEpisodeService {

    @GET("/episodes/{id}")
    suspend fun episode(@Path("id") id: Int): EpisodeData

    @GET("/episodes/{id}")
    fun episodeCall(@Path("id") id: Int): Call<EpisodeData>
}