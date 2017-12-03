package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.Images
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TvdbImagesService {

    @GET("/series/{tvdb_id}/images/query")
    fun images(@Path("tvdb_id") tvdbId: Int, @Query("keyType") type: String = "poster"): Flowable<Images>

}