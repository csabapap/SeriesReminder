package hu.csabapap.seriesreminder.data.repositories.episodes

import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Episode
import hu.csabapap.seriesreminder.data.network.services.EpisodesService
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class RemoteEpisodesDataSource @Inject constructor(
        private val episodesService: EpisodesService,
        private val tvdbApi: TvdbApi) {

    suspend fun getEpisode(showId: Int, seasonNumber: Int, episodeNumber: Int) : SREpisode {
        val episode = episodesService.episode(showId, seasonNumber, episodeNumber)
        val srEpisode = mapToSREpisode(episode, showId)
        val images = tvdbApi.episode(episode.ids.tvdb)
        return srEpisode.copy(image = images.data.filename)
    }

    private fun mapToSREpisode(episode: Episode, showId: Int) : SREpisode {
        return SREpisode(null,
                episode.season,
                episode.number,
                episode.title,
                episode.ids.trakt,
                episode.ids.tvdb,
                episode.absNumber,
                episode.overview,
                OffsetDateTime.parse(episode.firstAired),
                episode.updatedAt,
                episode.rating,
                episode.votes,
                "",
                showId)
    }

}