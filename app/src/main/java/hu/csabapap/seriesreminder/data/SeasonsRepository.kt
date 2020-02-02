package hu.csabapap.seriesreminder.data

import androidx.lifecycle.LiveData
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.db.daos.SeasonsDao
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.relations.SeasonWithEpisodes
import hu.csabapap.seriesreminder.data.exceptions.ItemNotFoundException
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.data.network.entities.Season
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class SeasonsRepository @Inject constructor(private val seasonsDao: SeasonsDao,
                                            private val traktApi: TraktApi,
                                            private val tvdbApi: TvdbApi,
                                            private val episodesRepository: EpisodesRepository) {

    suspend fun getSeasonImages(tvdbId: Int): Map<String, Image?> {
        val images = tvdbApi.images(tvdbId, "season")
        if (images != null) {
            return images.data.groupBy {
                it.subKey
            }.mapValues { it.value.maxBy { image -> image.ratingsInfo.average } }
        }
        return emptyMap()
    }

    suspend fun getSeasonsFromDb(showId: Int): List<SRSeason>? {
        return seasonsDao.getSeasons(showId)
    }

    suspend fun getSeasonByNumber(showId: Int, number: Int): SRSeason? {
        return seasonsDao.getSeason(showId, number)
    }

    suspend fun getSeasonsFromWeb(showId: Int): List<SRSeason>? {
        val seasons = traktApi.seasons(showId).await() ?: emptyList()
        return seasons.map {
            mapToSRSeasons(it, showId)
        }
    }

    fun getSeasonsLiveData(showId: Int): LiveData<List<SRSeason>> {
        return seasonsDao.getSeasonsLiveData(showId)
    }

    private fun mapToSRSeasons(season: Season, showId: Int): SRSeason {
        val episodes = mutableListOf<SREpisode>()
        if (season.episodes.isEmpty().not()) {
            season.episodes.forEach {
                episodes.add(episodesRepository.mapToSREpisode(it, showId))
            }
        }
        val srSeason = SRSeason(null,
                season.number,
                season.ids.trakt,
                season.episodeCount,
                season.airedEpisodes,
                showId)
        srSeason.episodes = episodes
        return srSeason
    }

    suspend fun getSeason(showId: Int, season: Int): SRSeason {
        return seasonsDao.getSeason(showId, season) ?: throw ItemNotFoundException("season not found in db")
    }

    suspend fun updateSeason(season: SRSeason) {
        seasonsDao.update(season)
    }

    suspend fun insertOrUpdateSeasons(localSeasons: List<SRSeason>, remoteSeasons: List<SRSeason>) {
        val localSeasonsMap = localSeasons.associateBy { it.traktId }

        val seasonsToSave = remoteSeasons.map {
            val localSeason = localSeasonsMap[it.traktId]
            if (localSeason != null) {
                return@map it.copy(id= localSeason.id, nmbOfWatchedEpisodes = localSeason.nmbOfWatchedEpisodes)
            }
            it
        }

        seasonsDao.upsert(seasonsToSave)
    }

    suspend fun getSeasonWithEpisodes(showId: Int, seasonNumber: Int): SeasonWithEpisodes? {
        return seasonsDao.getSeasonWithEpisodes(showId, seasonNumber)
    }

}