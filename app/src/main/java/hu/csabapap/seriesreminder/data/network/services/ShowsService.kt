package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShowsService{
    @GET("shows/trending")
    fun trendingShows(@Query("extended") extended: String = "") : Flowable<List<TrendingShow>>

    @GET("shows/popular?extended=full")
    fun popularShows() : Flowable<List<Show>>

    @GET("shows/{id}?extended=full")
    fun show(@Path("id") traktId: Int) : Flowable<Show>
}
