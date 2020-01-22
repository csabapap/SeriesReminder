package hu.csabapap.seriesreminder.data.repositories.nextepisodes

import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry
import hu.csabapap.seriesreminder.data.network.entities.NextEpisode
import hu.csabapap.seriesreminder.data.states.NextEpisodeState
import hu.csabapap.seriesreminder.data.states.NextEpisodeSuccess
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NextEpisodesRepository @Inject constructor(
        private val localDataSource: NextEpisodesLocalDataSource,
        private val remoteDataSource: NextEpisodesRemoteDataSource) {

    suspend fun fetchAndSaveNextEpisode(showId: Int): NextEpisodeState {
        val state = remoteDataSource.fetchNextEpisode(showId)

        if (state is NextEpisodeSuccess) {
            localDataSource.saveNextEpisode(mapToNextEpisodeEntry(state.nextEpisode, showId))
        }

        return state
    }

    private fun mapToNextEpisodeEntry(nextEpisode: NextEpisode, showId: Int): NextEpisodeEntry {
        return NextEpisodeEntry(null,
                nextEpisode.season,
                nextEpisode.number,
                nextEpisode.title,
                nextEpisode.ids.trakt,
                nextEpisode.ids.tvdb,
                showId)
    }

}