package hu.csabapap.seriesreminder.domain

import com.uwetrottmann.trakt5.TraktV2
import com.uwetrottmann.trakt5.entities.EpisodeIds
import com.uwetrottmann.trakt5.entities.SyncEpisode
import com.uwetrottmann.trakt5.entities.SyncItems
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.repositories.WatchedEpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject

class SetEpisodeWatchedUseCase @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val seasonsRepository: SeasonsRepository,
        private val watchedEpisodesRepository: WatchedEpisodesRepository,
        private val loggedInUserRepository: LoggedInUserRepository,
        private val traktTv: TraktV2
) {

    suspend operator fun invoke(episode: SREpisode) {
        val episodeId = episode.id ?: throw Error("missing episode id")
        val watchedAt = OffsetDateTime.now()
        val watchedEpisode = WatchedEpisode(null, episode.showId, episode.season, episode.number, episodeId, watchedAt)
        val result = watchedEpisodesRepository.setEpisodeWatched(watchedEpisode)
        if (result == -1L) return
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