package hu.csabapap.seriesreminder.data.repositories.episodes

import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.db.daos.EpisodeDao
import hu.csabapap.seriesreminder.data.db.daos.NextEpisodeDao
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Episode
import hu.csabapap.seriesreminder.data.network.entities.EpisodeData
import hu.csabapap.seriesreminder.utils.safeApiCall
import io.reactivex.Single
import kotlinx.coroutines.rx2.await
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class EpisodesRepository @Inject constructor(
        private val localDataSource: LocalEpisodesDataSource,
        private val remoteDataSource: RemoteEpisodesDataSource,
        private val tvdbApi: TvdbApi,
        private val nextEpisodeDao: NextEpisodeDao,
        private val episodesDao: EpisodeDao) {

    suspend fun getEpisode(showId: Int, season: Int, number: Int ): EpisodeWithShow? {
        return localDataSource.getBySeasonAndEpisodeNumber(showId, season, number)
    }

    suspend fun getEpisodeById(episodeId: Long): SREpisode? {
        return localDataSource.getById(episodeId)
    }

    suspend fun getNextEpisode(showId: Int, absNumber: Int ): SREpisode? {
        return localDataSource.get(showId, absNumber)
    }

    fun insertNextEpisode(nextEpisodeEntry: NextEpisodeEntry) {
        nextEpisodeDao.insert(nextEpisodeEntry)
    }

    fun fetchEpisodeImage(episode: SREpisode): Single<SREpisode> {
        return tvdbApi.episodeSingle(episode.tvdbId)
                .map {
                    var filename = "-1"
                    if (it.data.filename.isEmpty().not()) {
                        filename = it.data.filename
                    }
                    episode.copy(image = filename)
                }
                .doOnSuccess {
                    if (it.image.isEmpty().not()) {
                        updateEpisode(it)
                    }
                }
    }

    suspend fun fetchEpisodeImage(tvdbId: Int): Result<String> {
        return safeApiCall({
            val episode = tvdbApi.episode(tvdbId)
            return@safeApiCall Result.Success(episode.data.filename)
        }, "fetch EpisodeData error from TheTvdb")
    }

    private fun updateEpisode(episode: SREpisode) {
        episodesDao.update(episode)
    }

    fun getNextEpisode(showId: Int) = nextEpisodeDao.getNextEpisode(showId)

    fun getNextEpisodes(limit: Int) = nextEpisodeDao.getNextEpisodes(limit)

    suspend fun getNextEpisodes() = nextEpisodeDao.getNextEpisodes().await()


    fun getEpisodeInfoFromTvdb(tvdbId: Int) = tvdbApi.episodeSingle(tvdbId)

    fun saveEpisode(episode: SREpisode) {
        localDataSource.save(episode)
    }

    fun saveEpisodes(episodes: List<SREpisode>) {
        localDataSource.save(episodes)
    }

    fun saveImage(tvdbId: Int, url: String) {
        localDataSource.saveImage(tvdbId, url)
    }

    fun mapToSREpisode(episode: Episode, showId: Int) : SREpisode {
        return SREpisode(null,
                episode.season,
                episode.number,
                episode.title,
                episode.ids.trakt,
                episode.ids.tvdb,
                episode.absNumber,
                episode.overview,
                OffsetDateTime.parse(episode.firstAired),
                episode.updatedAt,
                episode.rating,
                episode.votes,
                "",
                showId,
                -1)
    }

    suspend fun getEpisodesBySeason(showId: Int, seasonNumber: Int): List<SREpisode> {
        return localDataSource.getEpisodesForSeason(showId, seasonNumber)
    }
}