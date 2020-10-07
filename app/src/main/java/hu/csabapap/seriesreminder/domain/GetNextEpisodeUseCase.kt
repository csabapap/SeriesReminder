package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import javax.inject.Inject

class GetNextEpisodeUseCase @Inject constructor(
        private val episodesRepository: EpisodesRepository
) {
    suspend fun getNextEpisode(showId: Int): Boolean {
        val localEpisode = episodesRepository.getNextUpcomingEpisode(showId)
        return localEpisode != null
    }
}