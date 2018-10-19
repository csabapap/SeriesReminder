package hu.csabapap.seriesreminder.data.network

import hu.csabapap.seriesreminder.data.network.entities.*
import hu.csabapap.seriesreminder.data.network.services.EpisodesService
import hu.csabapap.seriesreminder.data.network.services.SearchService
import hu.csabapap.seriesreminder.data.network.services.SeasonsService
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit

class TraktApi(private val retrofit: Retrofit) {

    fun trendingShows(extended: String = ""): Single<List<TrendingShow>> {
        return retrofit.create(ShowsService::class.java).trendingShows(extended)
    }

    fun paginatedTrendingShows(extended: String = "", page: Int = 1, limit: Int = 20): Single<List<TrendingShow>> {
        return retrofit.create(ShowsService::class.java).paginatedTrendingShows(extended, page, limit)
    }

    fun popularShows(extended: String = "", page: Int = 1, limit: Int = 20) =
        retrofit.create(ShowsService::class.java).popularShows(extended, page, limit)


    fun show(traktId: Int) : Single<Show>{
        return retrofit.create(ShowsService::class.java).show(traktId)
    }

    fun seasons(showId: Int): Single<List<Season>> {
        return retrofit.create(SeasonsService::class.java).seasons(showId)
    }

    fun nextEpisode(traktId: Int) : Single<Response<NextEpisode>> {
        return retrofit.create(ShowsService::class.java).nextEpisode(traktId)
    }

    fun episode(showId: Int, seasonNumber: Int, number: Int) : Single<Response<Episode>> {
        return retrofit.create(EpisodesService::class.java).episode(showId, seasonNumber, number)
    }

    fun search(type: String, query: String): Single<List<SearchResult>> {
        return retrofit.create(SearchService::class.java).search(type, query)
    }
}