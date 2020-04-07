package hu.csabapap.seriesreminder.data.network

import hu.csabapap.seriesreminder.data.network.entities.*
import hu.csabapap.seriesreminder.data.network.services.EpisodesService
import hu.csabapap.seriesreminder.data.network.services.SearchService
import hu.csabapap.seriesreminder.data.network.services.SeasonsService
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.rx2.await
import retrofit2.Response
import retrofit2.Retrofit

class TraktApi(private val retrofit: Retrofit) {

    fun show(traktId: Int) : Single<Show>{
        return retrofit.create(ShowsService::class.java).show(traktId)
    }

    fun seasons(showId: Int): Single<List<Season>> {
        return retrofit.create(SeasonsService::class.java).seasons(showId)
    }

    fun nextEpisode(traktId: Int): Deferred<Response<NextEpisode>> {
        return retrofit.create(ShowsService::class.java).nextEpisode(traktId)
    }

    fun search(query: String, type: String): Single<List<SearchResult>> {
        return retrofit.create(SearchService::class.java).search(type, query)
    }
}