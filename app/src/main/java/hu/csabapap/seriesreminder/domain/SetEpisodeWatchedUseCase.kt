package hu.csabapap.seriesreminder.domain

import com.uwetrottmann.trakt5.TraktV2
import com.uwetrottmann.trakt5.entities.EpisodeIds
import com.uwetrottmann.trakt5.entities.SyncEpisode
import com.uwetrottmann.trakt5.entities.SyncItems
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import timber.log.Timber
import javax.inject.Inject

class SetEpisodeWatchedUseCase @Inject constructor(
        private val saveWatchedEpisodeInDbUseCase: SaveWatchedEpisodeInDbUseCase,
        private val loggedInUserRepository: LoggedInUserRepository,
        private val traktTv: TraktV2
) {

    suspend operator fun invoke(episode: SREpisode) {
        val result = saveWatchedEpisodeInDbUseCase(episode)
        if (result == -1L) return
        if (loggedInUserRepository.isLoggedIn()) {
            val syncEpisode = SyncEpisode()
                    .id(EpisodeIds.trakt(episode.traktId))
            val response = traktTv.sync().addItemsToWatchedHistory(SyncItems().episodes(syncEpisode)).execute()

            if (response.isSuccessful) {
                Timber.d("saved in trakt")
            }
        }
    }
}