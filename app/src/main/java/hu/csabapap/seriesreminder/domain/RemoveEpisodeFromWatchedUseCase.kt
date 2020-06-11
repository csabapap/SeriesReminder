package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.repositories.WatchedEpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import javax.inject.Inject

class RemoveEpisodeFromWatchedUseCase @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val seasonsRepository: SeasonsRepository,
        private val episodesRepository: EpisodesRepository,
        private val watchedEpisodesRepository: WatchedEpisodesRepository) {

    suspend operator fun invoke(watchedEpisode: WatchedEpisode) {
        val watchedEpisodeFromDb = watchedEpisodesRepository.getWatchedEpisode(
                watchedEpisode.showId, watchedEpisode.season, watchedEpisode.number)
        val affectedRows = watchedEpisodesRepository.removeEpisodeFromWatched(watchedEpisodeFromDb)
        if (affectedRows == 0) return

        updateSeason(watchedEpisode)
        updateShow(watchedEpisode)
    }

    private suspend fun updateSeason(watchedEpisode: WatchedEpisode) {
        val season = seasonsRepository.getSeason(watchedEpisode.showId, watchedEpisode.season)
        val nmbOfWatchedEpisodes = season.nmbOfWatchedEpisodes - 1
        val updatedSeason = season.copy(nmbOfWatchedEpisodes = nmbOfWatchedEpisodes)
        seasonsRepository.updateSeason(updatedSeason)
    }

    private suspend fun updateShow(watchedEpisode: WatchedEpisode) {
        val episodeWithShow = episodesRepository.getEpisode(watchedEpisode.showId, watchedEpisode.season, watchedEpisode.number)
        if (episodeWithShow != null) {
            val show = episodeWithShow.show
            val episode = episodeWithShow.episode
            if (show != null) {
                if (show.nextEpisode == episode.absNumber + 1) {
                    showsRepository.updateNextEpisode(episode.showId, episode.absNumber)
                }
            }
        }
    }
}