package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.network.entities.NextEpisode
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import hu.csabapap.seriesreminder.utils.safeApiCall
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShowsService{
    @GET("shows/trending")
    fun trendingShows(@Query("extended") extended: String)
            : Single<List<TrendingShow>>

    @GET("shows/trending")
    fun paginatedTrendingShows(@Query("extended") extended: String,
                      @Query("page") page: Int,
                      @Query("limit") limit: Int)
            : Single<List<TrendingShow>>

    @GET("shows/trending")
    fun deferredPaginatedTrendingShows(@Query("extended") extended: String,
                               @Query("page") page: Int,
                               @Query("limit") limit: Int)
            : Deferred<List<TrendingShow>>

    @GET("shows/popular?extended=full")
    suspend fun popularShows(@Query("extended") extended: String,
                     @Query("page") page: Int,
                     @Query("limit") limit: Int): Result<List<Show>>

    @GET("shows/{id}?extended=full")
    fun showSingle(@Path("id") traktId: Int) : Single<Show>

    @GET("shows/{id}?extended=full")
    suspend fun show(@Path("id") traktId: Int) : Response<Show>

    @GET("shows/{id}/next_episode")
    fun nextEpisode(@Path("id") traktId: Int) : Deferred<Response<NextEpisode>>
}
