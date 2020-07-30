package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.Images
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TvdbImagesService {

    @GET("/series/{tvdb_id}/images/query")
    suspend fun images(@Path("tvdb_id") tvdbId: Int,
                       @Query("keyType") type: String = "poster"): Images?

    @GET("/series/{tvdb_id}/images/query")
    fun imagesCall(@Path("tvdb_id") tvdbId: Int, @Query("keyType") type: String = "poster")
            : Call<Images>
}