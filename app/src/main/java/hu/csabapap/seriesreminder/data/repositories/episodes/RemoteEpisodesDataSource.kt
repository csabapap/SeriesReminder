package hu.csabapap.seriesreminder.data.repositories.episodes

import com.uwetrottmann.trakt5.enums.Extended
import com.uwetrottmann.trakt5.services.Episodes
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.network.TvdbApi
import timber.log.Timber
import javax.inject.Inject

class RemoteEpisodesDataSource @Inject constructor(
        private val episodes: Episodes,
        private val tvdbApi: TvdbApi) {

    suspend fun getEpisode(showId: Int, seasonNumber: Int, episodeNumber: Int) : SREpisode {
        val episode = episodes.summary(showId.toString(), seasonNumber, episodeNumber, Extended.FULL).execute().body()
        val srEpisode = mapToSREpisode(episode!!, showId)
        try {
            val images = tvdbApi.episode(episode.ids.tvdb)
            return srEpisode.copy(image = images.data.filename)
        } catch (e: Exception) {
            Timber.e(e)
        }
        return srEpisode
    }

    private fun mapToSREpisode(episode: com.uwetrottmann.trakt5.entities.Episode, showId: Int) : SREpisode {
        return SREpisode(null,
                episode.season,
                episode.number,
                episode.title,
                episode.ids.trakt,
                episode.ids.tvdb,
                episode.number_abs,
                episode.overview,
                episode.first_aired,
                episode.updated_at.toString(),
                episode.rating.toFloat(),
                episode.votes,
                "",
                showId)
    }

}