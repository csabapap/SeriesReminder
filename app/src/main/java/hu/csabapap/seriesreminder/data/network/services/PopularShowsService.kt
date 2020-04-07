package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PopularShowsService {

    @GET("shows/popular")
    suspend fun popularShows(@Query("extended") extended: String)
            : Response<List<Show>>

    @GET("shows/popular")
    suspend fun paginatedPopularShows(@Query("extended") extended: String,
                                       @Query("page") page: Int,
                                       @Query("limit") limit: Int)
            : Response<List<Show>>

}