package hu.csabapap.seriesreminder.data.repositories.episodes

import com.uwetrottmann.trakt5.entities.Episode
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.db.daos.NextEpisodeDao
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.utils.safeApiCall
import javax.inject.Inject

class EpisodesRepository @Inject constructor(
        private val localDataSource: LocalEpisodesDataSource,
        private val remoteDataSource: RemoteEpisodesDataSource,
        private val tvdbApi: TvdbApi,
        private val nextEpisodeDao: NextEpisodeDao) {

    suspend fun getEpisode(showId: Int, season: Int, number: Int ): EpisodeWithShow? {
        return localDataSource.getBySeasonAndEpisodeNumber(showId, season, number)
    }

    suspend fun getEpisodes(showId: Int): List<SREpisode> {
        return localDataSource.getAllEpisodesForShow(showId)
    }

    suspend fun getEpisodeFromTrakt(showId: Int, season: Int, number: Int): SREpisode? {
        return remoteDataSource.getEpisode(showId, season, number)
    }

    suspend fun getEpisodeByAbsNumber(showId: Int, absNumber: Int ): SREpisode? {
        return localDataSource.get(showId, absNumber)
    }

    suspend fun fetchEpisodeImage(tvdbId: Int): Result<String> {
        return safeApiCall({
            val episode = tvdbApi.episode(tvdbId)
            return@safeApiCall Result.Success(episode.data.filename)
        }, "fetch EpisodeData error from TheTvdb")
    }

    fun getUpcomingEpisodesFlow(limit: Int = 3) = localDataSource.getUpcomingEpisodesFlow(limit)

    suspend fun getNextEpisodes() = nextEpisodeDao.getNextEpisodeInWatchList()

    suspend fun getNextUpcomingEpisode(showId: Int) = localDataSource.getNextUpcomingEpisode(showId)

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
                episode.number_abs ?: 0,
                episode.overview,
                episode.first_aired,
                episode.updated_at.toString(),
                episode.rating.toFloat(),
                episode.votes,
                "",
                showId,
                -1)
    }

    suspend fun getEpisodesBySeason(showId: Int, seasonNumber: Int): List<SREpisode> {
        return localDataSource.getEpisodesForSeason(showId, seasonNumber)
    }

    suspend fun getUpcomingEpisode(showId: Int): EpisodeWithShow? {
        return localDataSource.getUpcomingEpisode(showId)
    }
}