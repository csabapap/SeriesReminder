package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.SeasonsDao
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.entities.Season
import io.reactivex.Single
import javax.inject.Inject

class SeasonsRepository @Inject constructor(private val seasonsDao: SeasonsDao,
                                            private val traktApi: TraktApi) {

    fun getSeasons(showId: Int): Single<List<SRSeason>> {
        return traktApi.seasons(showId)
                .toFlowable()
                .flatMapIterable { it }
                .map { mapToSRSeasons(it, showId) }
                .doAfterNext { seasonsDao.insert(it) }
                .toList()
    }

    private fun mapToSRSeasons(season: Season, showId: Int): SRSeason {
        return SRSeason(null,
                season.number,
                season.ids.trakt,
                season.episodeCount,
                season.airedEpisodes,
                showId)
    }

}