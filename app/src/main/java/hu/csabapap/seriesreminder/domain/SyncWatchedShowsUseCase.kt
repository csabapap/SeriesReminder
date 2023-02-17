package hu.csabapap.seriesreminder.domain

import com.uwetrottmann.trakt5.entities.UserSlug
import com.uwetrottmann.trakt5.services.Users
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.utils.safeApiCall
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.OffsetDateTime
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class SyncWatchedShowsUseCase @Inject constructor(
        private val loggedInUserRepository: LoggedInUserRepository,
        private val collectionRepository: CollectionRepository,
        private val traktUsers: Users,
        private val getWatchedShowUseCase: GetWatchedShowUseCase,
        private val showsRepository: ShowsRepository,
        private val seasonsRepository: SeasonsRepository,
        private val episodesRepository: EpisodesRepository,
        private val saveWatchedEpisodeInDbUseCase: SaveWatchedEpisodeInDbUseCase
) {
    suspend fun sync() {
        val start = System.currentTimeMillis()
        if (!loggedInUserRepository.isLoggedIn()) return

        val result = watchedShowsFromTrakt()
        if (result is Result.Error) {
            Timber.e(result.exception)
            return
        }
        if (result is Result.Success) {
            val watchedData = result.data
            coroutineScope {
                val watchedShowsFromTratk = watchedData.map {
                    async {
                        val id = it.show?.ids?.trakt ?: return@async null
                        getWatchedShowUseCase(id)
                    }
                }.awaitAll()

                val shows = watchedShowsFromTratk.mapNotNull { it?.show }
                showsRepository.insertShows(shows)
                val collectionEntries = shows.map {
                    CollectionEntry(showId = it.traktId, added = OffsetDateTime.now())
                }
                collectionRepository.saveAll(collectionEntries)

                val seasonsFromWeb = watchedShowsFromTratk.mapNotNull { it?.seasonsWithImages }
                val allSeasonsToSave = seasonsFromWeb.map { remoteSeasonWithImages ->
                    val localSeasonsMap = seasonsRepository.getSeasonsFromDb(remoteSeasonWithImages.showTraktId)
                            ?.associateBy { it.traktId } ?: emptyMap()
                    remoteSeasonWithImages.seasons.map { season ->
                        val localSeason = localSeasonsMap[season.traktId]
                        val image = remoteSeasonWithImages.seasonsImages[season.number.toString()]
                        season.copy(id = localSeason?.id,
                                fileName = image?.fileName ?: remoteSeasonWithImages.fallbackPoster,
                                thumbnail = image?.thumbnail ?: remoteSeasonWithImages.fallbackPoster,
                                nmbOfWatchedEpisodes = localSeason?.nmbOfWatchedEpisodes ?: 0)
                    }
                }.flatten()
                seasonsRepository.upsertSeasons(allSeasonsToSave)

                val episodesToSave = seasonsFromWeb.map { seasonsWithImages ->
                    val seasonsWithCheckedAbsNumber = setEpisodeAbsNumberIfNotExists(seasonsWithImages.seasons)

                    val episodes = seasonsWithCheckedAbsNumber.map { season ->
                        season.episodes
                    }
                            .flatten()

                    val localSeasons = seasonsRepository.getSeasonsFromDb(seasonsWithImages.showTraktId)?.associateBy { season -> season.number }
                            ?: return@map null
                    episodes.mapNotNull episodeMap@ { episode ->
                        val localSeason = localSeasons[episode. season] ?: return@episodeMap null
                        episode.copy(seasonId = localSeason.id!!)
                    }
                }
                        .filterNotNull()
                        .flatten()
                episodesRepository.saveEpisodes(episodesToSave)

                watchedData.mapNotNull { show ->
                    show.seasons?.mapNotNull { season ->
                        season.episodes?.map episodeMap@ { episode ->
                            val showId = show.show?.ids?.trakt ?: return@episodeMap
                            val seasonNumber = season.number ?: return@episodeMap
                            val episodeNumber = episode.number ?: return@episodeMap
                            val savedEpisode = episodesRepository.getEpisode(showId, seasonNumber, episodeNumber)
                            if (savedEpisode != null) {
                                saveWatchedEpisodeInDbUseCase(savedEpisode.episode)
                            }
                        }
                    }
                }
                Timber.d("nmb of watched shows from trakt (${watchedShowsFromTratk.size}) inserted in: ${System.currentTimeMillis() - start}ms")
            }
        }
    }

    private suspend fun watchedShowsFromTrakt() = safeApiCall({
        val response = traktUsers.watchedShows(UserSlug.ME, null).execute()
        if (response.isSuccessful) {
            return@safeApiCall Result.Success(response.body() ?: emptyList())
        }
        return@safeApiCall Result.Error(HttpException(response))
    }, "error during getting watched shows")

    private fun setEpisodeAbsNumberIfNotExists(seasons: List<SRSeason>): List<SRSeason> {
        var absNumber = 0
        return seasons.sortedBy { season -> season.number }
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
}