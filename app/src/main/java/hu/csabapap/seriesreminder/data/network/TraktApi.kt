package hu.csabapap.seriesreminder.data.network

import hu.csabapap.seriesreminder.data.network.entities.*
import hu.csabapap.seriesreminder.data.network.services.EpisodesService
import hu.csabapap.seriesreminder.data.network.services.SeasonsService
import hu.csabapap.seriesreminder.data.network.services.ShowsService
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class TraktApi(private val retrofit: Retrofit) {

    fun trendingShows(extended: String = "", limit: Int = 20) : Single<List<TrendingShow>> {
        return retrofit.create(ShowsService::class.java).trendingShows(extended, limit)
    }

    fun popularShows(limit: Int = 20) : Single<List<Show>>{
        return retrofit.create(ShowsService::class.java).popularShows(limit)
    }

    fun show(traktId: Int) : Flowable<Show>{
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
}