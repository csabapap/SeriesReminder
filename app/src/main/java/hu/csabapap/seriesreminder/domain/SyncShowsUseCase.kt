package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.db.daos.LastRequestDao
import hu.csabapap.seriesreminder.data.db.entities.LastRequest
import hu.csabapap.seriesreminder.data.db.entities.Request
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import timber.log.Timber
import javax.inject.Inject

class SyncShowsUseCase @Inject constructor(val showsRepository: ShowsRepository,
                                           private val seasonsRepository: SeasonsRepository,
                                           private val episodesRepository: EpisodesRepository,
                                           val collectionRepository: CollectionRepository,
                                           private val lastRequestDao: LastRequestDao,
                                           private val createNotificationAlarmUseCase: CreateNotificationAlarmUseCase) {


    suspend fun syncShows() {
        Timber.d("sync shows")
        coroutineScope {
            val lastSyncRequest = lastRequestDao.getLastRequestByType(Request.SYNC_SHOWS)
            if (lastRequestDao.isRequestAfter(lastSyncRequest, Duration.ofHours(8))) {
                return@coroutineScope
            }
            val collectionItems = collectionRepository.getCollectionsSuspendable()
            lastRequestDao.insert(LastRequest(0L, LastRequest.SYNC_SHOWS_ID, Request.SYNC_SHOWS, Instant.now()))
            collectionItems.map {collectionItem ->
                val show = collectionItem.show ?: return@map null
                async {
                    showsRepository.getShowWithImages(show.traktId, show.tvdbId)

                    val seasons = seasonsRepository.getSeasonsFromDb(show.traktId)
                    val seasonsFromWeb = seasonsRepository.getSeasonsFromWeb(show.traktId) ?: return@async
                    val images = seasonsRepository.getSeasonImages(show.tvdbId)

                    val seasonsWithImages = seasonsFromWeb.map { season ->
                        val image = images[season.number.toString()]
                        season.copy(fileName = image?.fileName ?: "", thumbnail = image?.thumbnail ?: "")
                    }

                    if (seasons != null) {
                        seasonsRepository.insertOrUpdateSeasons(seasons, seasonsWithImages)
                    }

                    val episodes = seasonsFromWeb.map { season ->
                        season.episodes
                    }
                            .flatten()

                    Timber.d("nmb of episodes: ${episodes.size}")

                    val localSeasons = seasonsRepository.getSeasonsFromDb(show.traktId)?.associateBy { season -> season.number }
                    coroutineScope {
                        val episodesWithImages = episodes.map { episode ->
                            async {
                                val result = episodesRepository.fetchEpisodeImage(show.tvdbId)
                                val episodeImage = when (result) {
                                    is Result.Success -> result.data
                                    is Result.Error -> ""
                                }

                                episode.copy(image = episodeImage)
                            }
                        }.awaitAll()

                        val localEpisodes = episodesRepository.getEpisodes(show.traktId).associateBy { it.traktId }
                        val episodesToSave = episodesWithImages.map seasonIdCopy@{
                            val season = localSeasons?.get(it.season)
                            val episodeWithSeasonId = if (season?.id != null) {
                                it.copy(seasonId = season.id)
                            } else {
                                it
                            }
                            val localEpisode = localEpisodes[it.traktId]
                            val episodeWithId = if (localEpisode != null) {
                                 episodeWithSeasonId.copy(id = localEpisode.id)
                            } else {
                                episodeWithSeasonId
                            }
                            episodeWithId
                        }
                        episodesRepository.saveEpisodes(episodesToSave)
                    }
                    createAlarm(show.traktId)
                }
            }
                    .filterNotNull()
                    .awaitAll()
        }
    }

    private suspend fun createAlarm(showId: Int) {
        createNotificationAlarmUseCase.updateReminderAlarm(showId)
    }
}