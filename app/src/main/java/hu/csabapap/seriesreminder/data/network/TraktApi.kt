package hu.csabapap.seriesreminder.data.network

import hu.csabapap.seriesreminder.data.network.entities.*
import hu.csabapap.seriesreminder.data.network.services.SearchService
import hu.csabapap.seriesreminder.data.network.services.SeasonsService
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import io.reactivex.Single
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

    fun search(query: String, type: String): Single<List<SearchResult>> {
        return retrofit.create(SearchService::class.java).search(type, query)
    }
}