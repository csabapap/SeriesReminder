package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.daos.LastRequestDao
import hu.csabapap.seriesreminder.data.db.entities.LastRequest
import hu.csabapap.seriesreminder.data.db.entities.Request
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.nextepisodes.NextEpisodesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.rx2.await
import org.threeten.bp.Instant
import timber.log.Timber
import javax.inject.Inject

class SyncShowsUseCase @Inject constructor(val showsRepository: ShowsRepository,
                                           private val seasonsRepository: SeasonsRepository,
                                           private val episodesRepository: EpisodesRepository,
                                           val collectionRepository: CollectionRepository,
                                           private val nextEpisodesRepository: NextEpisodesRepository,
                                           private val lastRequestDao: LastRequestDao) {


    suspend fun syncShows() {
        Timber.d("sync shows")
        coroutineScope {
            val collectionItems = collectionRepository.getCollectionsSuspendable()
            lastRequestDao.insert(LastRequest(0L, LastRequest.SYNC_SHOWS_ID, Request.SHOW_DETAILS, Instant.now()))
            collectionItems.map {collectionItem ->
                val show = collectionItem.show ?: return@map null
                async {
                    showsRepository.getShowWithImages(show.traktId, show.tvdbId).await()

                    val seasons = seasonsRepository.getSeasonsFromDb(show.traktId)
                    val seasonsFromWeb = seasonsRepository.getSeasonsFromWeb(show.traktId)
                    val images = seasonsRepository.getSeasonImages(show.tvdbId)

                    val seasonsWithImages = seasonsFromWeb?.map { season ->
                        val image = images[season.number.toString()]
                        season.copy(fileName = image?.fileName ?: "", thumbnail = image?.thumbnail ?: "")
                    }

                    if (seasons != null && seasonsWithImages != null) {
                        seasonsRepository.insertOrUpdateSeasons(seasons, seasonsWithImages)
                    }

                    if (seasonsFromWeb == null) return@async
                    val episodes = seasonsFromWeb.map { season ->
                        season.episodes
                    }
                            .flatten()

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

                        episodesRepository.saveEpisodes(episodesWithImages)
                    }

                        nextEpisodesRepository.fetchAndSaveNextEpisode(show.traktId)
                }
            }
                    .filterNotNull()
                    .awaitAll()

        }
    }

}