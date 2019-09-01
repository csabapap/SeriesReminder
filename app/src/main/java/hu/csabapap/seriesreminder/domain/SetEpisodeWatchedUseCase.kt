package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.repositories.WatchedEpisodesRepository
import javax.inject.Inject

class SetEpisodeWatchedUseCase @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val seasonsRepository: SeasonsRepository,
        private val watchedEpisodesRepository: WatchedEpisodesRepository) {

    suspend operator fun invoke(episode: SREpisode) {
        val watchedEpisode = WatchedEpisode(null, episode.showId, episode.season, episode.number)
        val result = watchedEpisodesRepository.setEpisodeWatched(watchedEpisode)
        if (result == -1L) return
        val nextEpisodeAbsNumber = episode.absNumber + 1
        showsRepository.updateNextEpisode(episode.showId, nextEpisodeAbsNumber)

        val season = seasonsRepository.getSeason(episode.showId, episode.season)
        val nmbOfWatchedEpisodes = season.nmbOfWatchedEpisodes + 1
        if (nmbOfWatchedEpisodes <= season.airedEpisodeCount) {
            seasonsRepository.updateSeason(season.copy(nmbOfWatchedEpisodes = nmbOfWatchedEpisodes))
        }
    }
}