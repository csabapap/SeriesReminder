package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TrendingShowsService {

    @GET("shows/trending")
    suspend fun trendingShows(@Query("extended") extended: String)
            : Response<List<TrendingShow>>

    @GET("shows/trending")
    suspend fun paginatedTrendingShows(@Query("extended") extended: String,
                                       @Query("page") page: Int,
                                       @Query("limit") limit: Int)
            : Response<List<TrendingShow>>

}