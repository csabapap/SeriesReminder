package hu.csabapap.seriesreminder.data

import androidx.lifecycle.LiveData
import com.uwetrottmann.trakt5.entities.Season
import com.uwetrottmann.trakt5.enums.Extended
import com.uwetrottmann.trakt5.services.Seasons
import com.uwetrottmann.trakt5.services.Shows
import hu.csabapap.seriesreminder.data.db.daos.SeasonsDao
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.exceptions.ItemNotFoundException
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.utils.safeApiCall
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class SeasonsRepository @Inject constructor(private val seasonsDao: SeasonsDao,
                                            private val traktSeasons: Seasons,
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
            val seasonsResponse = traktSeasons.summary(showId.toString(), Extended.FULLEPISODES).execute()
            if (seasonsResponse.isSuccessful) {
                return@safeApiCall Result.Success(data = seasonsResponse.body() ?: emptyList())
            }
            return@safeApiCall Result.Error(HttpException(seasonsResponse))
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
                season.episode_count,
                season.aired_episodes,
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