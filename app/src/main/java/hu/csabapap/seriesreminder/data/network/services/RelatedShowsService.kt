package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.TraktRelatedShow
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface RelatedShowsService {
    @GET("shows/{id}/related")
    fun getRelatedShows(@Path("id") id: Int): Deferred<List<TraktRelatedShow>>
}