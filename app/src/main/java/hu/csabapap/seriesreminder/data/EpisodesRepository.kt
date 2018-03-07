package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.EpisodeDao
import hu.csabapap.seriesreminder.data.db.daos.NextEpisodeDao
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Episode
import hu.csabapap.seriesreminder.data.states.EpisodeError
import hu.csabapap.seriesreminder.data.states.EpisodeState
import hu.csabapap.seriesreminder.data.states.EpisodeSuccess
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class EpisodesRepository @Inject constructor(
        private val traktApi: TraktApi,
        private val tvdbApi: TvdbApi,
        private val nextEpisodeDao: NextEpisodeDao,
        private val episodesDao: EpisodeDao) {

    fun insertNextEpisode(nextEpisodeEntry: NextEpisodeEntry) {
        nextEpisodeDao.insert(nextEpisodeEntry)
    }

    fun getEpisode(showId: Int, seasonNumber: Int, episodeNumber: Int) : Single<EpisodeState> {
        return traktApi.episode(showId, seasonNumber, episodeNumber)
                .map<EpisodeState> { EpisodeSuccess(mapToSREpisode(it.body()!!, showId)) }
                .flatMap {
                    if (it is EpisodeSuccess) {
                        tvdbApi.episode(it.episode.tvdbId)
                                .map { episodeData ->
                                    Timber.d("$episodeData")
                                    val srEpisode = it.episode.copy(image = episodeData.data.filename)
                                    EpisodeSuccess(srEpisode)
                                }
                    } else {
                        Single.just(it)
                    }
                }
                .doOnSuccess({
                    if (it is EpisodeSuccess) {
                        episodesDao.insert(it.episode)
                    }
                })
                .onErrorReturn({
                    Timber.e(it)
                    EpisodeError })
    }

    private fun mapToSREpisode(episode: Episode, showId: Int) : SREpisode {
        Timber.d("$episode")
        return SREpisode(null,
                episode.season,
                episode.number,
                episode.title,
                episode.ids.trakt,
                episode.ids.tvdb,
                episode.absNumber,
                episode.overview,
                episode.firstAired,
                episode.updatedAt,
                episode.rating,
                episode.votes,
                "",
                showId)
    }

    fun getNextEpisode(showId: Int) = nextEpisodeDao.getNextEpisode(showId)

    fun getNextEpisodes() = nextEpisodeDao.getNextEpisodes()

    fun getEpisodeInfoFromTvdb(tvdbId: Int) = tvdbApi.episode(tvdbId)
}