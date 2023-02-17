package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.repositories.WatchedEpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class SaveWatchedEpisodeInDbUseCase @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val seasonsRepository: SeasonsRepository,
        private val watchedEpisodesRepository: WatchedEpisodesRepository,
) {

    suspend operator fun invoke(episode: SREpisode): Long {
        val episodeId = episode.id ?: throw Error("missing episode id")
        val watchedAt = OffsetDateTime.now()
        val watchedEpisode = WatchedEpisode(null, episode.showId, episode.season, episode.number, episodeId, watchedAt)
        val result = watchedEpisodesRepository.setEpisodeWatched(watchedEpisode)
        if (result == -1L) return result
        val nextEpisodeAbsNumber = episode.absNumber + 1
        val show = showsRepository.getShow(episode.showId)
        if (show != null && show.nextEpisode < nextEpisodeAbsNumber) {
            showsRepository.updateNextEpisode(episode.showId, nextEpisodeAbsNumber)
        }

        val season = seasonsRepository.getSeason(episode.showId, episode.season)
        val nmbOfWatchedEpisodes = season.nmbOfWatchedEpisodes + 1
        if (nmbOfWatchedEpisodes <= season.airedEpisodeCount) {
            seasonsRepository.updateSeason(season.copy(nmbOfWatchedEpisodes = nmbOfWatchedEpisodes))
        }
        return result
    }
}