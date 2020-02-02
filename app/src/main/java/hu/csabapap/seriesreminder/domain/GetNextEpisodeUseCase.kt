package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.nextepisodes.NextEpisodesRepository
import hu.csabapap.seriesreminder.data.states.NextEpisodeSuccess
import javax.inject.Inject

class GetNextEpisodeUseCase @Inject constructor(
        private val nextEpisodesRepository: NextEpisodesRepository,
        private val seasonsRepository: SeasonsRepository,
        private val episodesRepository: EpisodesRepository
) {

    suspend fun getNextEpisode(showId: Int): Boolean {
        val state = nextEpisodesRepository.fetchNextEpisode(showId)
        if (state is NextEpisodeSuccess) {
            val nextEpisode = state.nextEpisode
            val localEpisode = episodesRepository.getEpisode(showId, nextEpisode.season, nextEpisode.number)
            if (localEpisode == null) {
                val season = seasonsRepository.getSeasonsFromDb(showId)
                val episode = episodesRepository.getEpisodeFromTrakt(showId, nextEpisode.season, nextEpisode.number)
                if (season != null && episode != null) {
                    episodesRepository.saveEpisode(episode)
                    nextEpisodesRepository.saveNextEpisode(showId, nextEpisode)
                }
            } else {
                nextEpisodesRepository.saveNextEpisode(showId, nextEpisode)
            }
            return true
        }

        return false
    }

}