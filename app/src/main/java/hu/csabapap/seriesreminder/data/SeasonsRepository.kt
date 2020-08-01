package hu.csabapap.seriesreminder.data

import androidx.lifecycle.LiveData
import hu.csabapap.seriesreminder.data.db.daos.SeasonsDao
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.exceptions.ItemNotFoundException
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.data.network.entities.Season
import hu.csabapap.seriesreminder.data.network.services.SeasonsService
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.utils.safeApiCall
import timber.log.Timber
import javax.inject.Inject

class SeasonsRepository @Inject constructor(private val seasonsDao: SeasonsDao,
                                            private val seasonsService: SeasonsService,
                                            private val tvdbApi: TvdbApi,
                                            private val episodesRepository: EpisodesRepository) {

    suspend fun getSeasonImages(tvdbId: Int): Map<String, Image?> {
        Timber.d("getSeasonsImages")
        val images = try {
            tvdbApi.images(tvdbId, "season")
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
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

    suspend fun getSeasonsFromWeb(showId: Int): List<SRSeason>? {
        Timber.d("getSeasonsFromWeb")
        val result = safeApiCall({
            val seasons = seasonsService.seasons(showId)
            Result.Success(data = seasons)
        }, errorMessage = "fetch seasons error")
        if (result is Result.Error) {
            return null
        }
        val seasons = (result as Result.Success).data
        var absNumber = 0
        return seasons.map {
            mapToSRSeasons(it, showId)
        }.sortedBy { season -> season.number }
                .map {season ->
                    if (season.number == 0) return@map season

                    season.episodes = season.episodes.sortedBy { episode -> episode.number }
                            .map episodeMap@{ episode ->
                                absNumber += 1
                                return@episodeMap if (episode.absNumber == 0) {
                                    episode.copy(absNumber = absNumber)
                                } else {
                                    episode
                                }
                            }
                    season
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
}