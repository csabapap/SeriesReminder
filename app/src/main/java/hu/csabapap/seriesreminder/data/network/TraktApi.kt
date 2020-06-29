package hu.csabapap.seriesreminder.data.network

import hu.csabapap.seriesreminder.data.network.entities.NextEpisode
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit

class TraktApi(private val retrofit: Retrofit) {

    suspend fun show(traktId: Int) : Response<Show>{
        return retrofit.create(ShowsService::class.java).showResponse(traktId)
    }

    fun nextEpisode(traktId: Int): Deferred<Response<NextEpisode>> {
        return retrofit.create(ShowsService::class.java).nextEpisode(traktId)
    }
}