package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.NextEpisode
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShowsService{
    @GET("shows/trending")
    fun trendingShows(@Query("extended") extended: String = "",
                      @Query("limit") limit: Int)
            : Single<List<TrendingShow>>

    @GET("shows/popular?extended=full")
    fun popularShows(@Query("limit") limit: Int) : Single<List<Show>>

    @GET("shows/{id}?extended=full")
    fun show(@Path("id") traktId: Int) : Flowable<Show>

    @GET("shows/{id}/next_episode")
    fun nextEpisode(@Path("id") traktId: Int) : Single<NextEpisode>
}
