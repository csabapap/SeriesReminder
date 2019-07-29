package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.SeasonsDao
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.exceptions.ItemNotFoundException
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.data.network.entities.Season
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SeasonsRepository @Inject constructor(private val seasonsDao: SeasonsDao,
                                            private val traktApi: TraktApi,
                                            private val tvdbApi: TvdbApi,
                                            private val episodesRepository: EpisodesRepository) {

    fun getSeasons(showId: Int): Single<List<SRSeason>> {
        return traktApi.seasons(showId)
                .toFlowable()
                .flatMapIterable { it }
                .map { mapToSRSeasons(it, showId) }
                .doAfterNext {
                    seasonsDao.insert(it)
                    it.episodes.forEach { episode ->
                        episodesRepository.saveEpisode(episode)
                    }
                }
                .toList()
    }

    suspend fun getSeasonImages(tvdbId: Int): Map<String, Image?> {
        val images = tvdbApi.images(tvdbId, "season")
        if (images != null) {
            return images.data.groupBy {
                it.subKey
            }.mapValues { it.value.maxBy { image -> image.ratingsInfo.average } }
        }
        return emptyMap()
    }

    suspend fun getSeasonsFromDb(showId: Int, showTvdbId: Int): List<SRSeason>? {
        val seasons = seasonsDao.getSeasons(showId)

        // TODO getting images not belongs here
        val images: Map<String, Image?> = try{
            getSeasonImages(showTvdbId)
        } catch (e: Exception) {
            emptyMap()
        }

        if (images.isNotEmpty()) {
            seasons?.map {
                val filename = images[it.number.toString()]
                Timber.d("filename: $filename")
            }
        }
        return seasons

    }

    suspend fun insertSeasons(season: List<SRSeason>) {
        season.forEach {
            seasonsDao.insert(it)
        }
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

}