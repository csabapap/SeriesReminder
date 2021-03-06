package hu.csabapap.seriesreminder.tasks

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject

class DownloadShowTask(private val showId: Int): Task {

    @Inject
    lateinit var showsRepository: ShowsRepository

    @Inject
    lateinit var seasonsRepository: SeasonsRepository

    @Inject
    lateinit var episodesRepository: EpisodesRepository

    @Inject
    lateinit var collectionRepository: CollectionRepository

    @Inject
    lateinit var tvdbApi: TvdbApi

    override suspend fun execute() {
        val show = showsRepository.getShow(showId) ?: return
        val posters = tvdbApi.images(show.tvdbId, "poster") ?: return
        val covers = tvdbApi.images(show.tvdbId, "fanart") ?: return

        val popularPoster = posters.data.maxBy { image ->
            image.ratingsInfo.average
        }
        val popularCover = covers.data.maxBy { image ->
            image.ratingsInfo.average
        }

        val newShow = show.copy(
                poster = popularPoster?.fileName ?: "",
                posterThumb = popularPoster?.thumbnail ?: "",
                cover = popularCover?.fileName ?: "",
                coverThumb = popularCover?.thumbnail ?: "")

        showsRepository.insertShow(newShow)
        val collectionEntry = CollectionEntry(showId = newShow.traktId, added = OffsetDateTime.now())
        collectionRepository.save(collectionEntry)

        val seasons = seasonsRepository.getSeasonsFromDb(show.traktId)
        val seasonsFromWeb = seasonsRepository.getSeasonsFromWeb(show.traktId) ?: return
        val images = seasonsRepository.getSeasonImages(newShow.tvdbId)

        val seasonsWithImages = seasonsFromWeb.map { season ->
            val image = images[season.number.toString()]
            season.copy(fileName = image?.fileName ?: "", thumbnail = image?.thumbnail ?: "")
        }

        if (seasons != null) {
            seasonsRepository.insertOrUpdateSeasons(seasons, seasonsWithImages)
        }

        val seasonsWithCheckedAbsNumber = setEpisodeAbsNumberIfNotExists(seasonsFromWeb)

        val episodes = seasonsWithCheckedAbsNumber.map { season ->
            season.episodes
        }
                .flatten()

        val localSeasons = seasonsRepository.getSeasonsFromDb(show.traktId)?.associateBy { season -> season.number }
                ?: return
        episodes.forEach { episode ->
            localSeasons[episode.season]?.apply {
                episodesRepository.saveEpisode(episode.copy(seasonId = id!!))
            }
        }

        Timber.d("seasons: $seasons")
    }

    fun setEpisodeAbsNumberIfNotExists(seasons: List<SRSeason>): List<SRSeason> {
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