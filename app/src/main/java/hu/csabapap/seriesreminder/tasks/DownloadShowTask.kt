package hu.csabapap.seriesreminder.tasks

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.repositories.nextepisodes.NextEpisodesRepository
import kotlinx.coroutines.rx2.await
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject

class DownloadShowTask(private val showId: Int): Task {

    @Inject
    lateinit var showsRepository: ShowsRepository

    @Inject
    lateinit var seasonsRepository: SeasonsRepository

    @Inject
    lateinit var nextEpisodesRepository: NextEpisodesRepository

    @Inject
    lateinit var collectionRepository: CollectionRepository

    @Inject
    lateinit var tvdbApi: TvdbApi

    override suspend fun execute() {
        val show = showsRepository.getShow(showId).await()
        show?.let {
            val posters = tvdbApi.imagesSingle(it.tvdbId, "poster").await()
            val covers = tvdbApi.imagesSingle(it.tvdbId, "fanart").await()

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
            val collectionId = collectionRepository.save(collectionEntry)


            val seasons = seasonsRepository.getSeasons(newShow.traktId).await()
            val images = seasonsRepository.getSeasonImages(newShow.tvdbId)

            val seasonsWithImages = seasons.map { season ->
                val image = images[season.number.toString()]
                season.copy(fileName = image?.fileName ?: "", thumbnail = image?.thumbnail ?: "")
            }

            seasonsRepository.insertSeasons(seasonsWithImages)

            Timber.d("seasons: $seasons")
            nextEpisodesRepository.fetchAndSaveNextEpisode(newShow.traktId)
        }
    }
}