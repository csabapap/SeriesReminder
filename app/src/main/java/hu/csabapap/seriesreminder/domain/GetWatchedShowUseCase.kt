package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import timber.log.Timber
import javax.inject.Inject

class GetWatchedShowUseCase @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val seasonsRepository: SeasonsRepository,
        private val tvdbApi: TvdbApi
) {

    suspend operator fun invoke(showId: Int): ShowWithSeasonsAndImagesFromTrakt? {
        val show = showsRepository.getShow(showId) ?: return null
        val posters = tvdbApi.images(show.tvdbId, "poster") ?: return null
        val covers = try {
            tvdbApi.images(show.tvdbId, "fanart")
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

        val popularPoster = posters.data.maxByOrNull { image ->
            image.ratingsInfo.average
        }
        val popularCover = covers?.data?.maxByOrNull { image ->
            image.ratingsInfo.average
        }

        val newShow = show.copy(
                poster = popularPoster?.fileName ?: "",
                posterThumb = popularPoster?.thumbnail ?: "",
                cover = popularCover?.fileName ?: "",
                coverThumb = popularCover?.thumbnail ?: "")

        val seasonsFromWeb = seasonsRepository.getSeasonsFromWeb(show.traktId) ?: return null
        val seasonImages = seasonsRepository.getSeasonImages(newShow.tvdbId)
        val fallbackSeasonPoster = popularPoster?.thumbnail ?: ""

        return ShowWithSeasonsAndImagesFromTrakt(newShow,
                SeasonsWithImages(
                        showId,
                        seasonsFromWeb,
                        seasonImages,
                        fallbackSeasonPoster))
    }
}

data class ShowWithSeasonsAndImagesFromTrakt(
        val show: SRShow,
        val seasonsWithImages: SeasonsWithImages
)

data class SeasonsWithImages(
        val showTraktId: Int,
        val seasons: List<SRSeason>,
        val seasonsImages: Map<String, Image?>,
        val fallbackPoster: String = ""
)