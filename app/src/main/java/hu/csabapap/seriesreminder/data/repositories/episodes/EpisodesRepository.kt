package hu.csabapap.seriesreminder.data.repositories.episodes

import com.uwetrottmann.trakt5.entities.Episode
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.db.daos.NextEpisodeDao
import hu.csabapap.seriesreminder.data.db.entities.AiringTime
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.extensions.diffInDays
import hu.csabapap.seriesreminder.extensions.diffInHours
import hu.csabapap.seriesreminder.ui.main.home.UpcomingEpisode
import hu.csabapap.seriesreminder.utils.getAirDateTimeInCurrentTimeZone
import hu.csabapap.seriesreminder.utils.safeApiCall
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDateTime
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
                episode.ids?.tvdb ?: -1,
                episode.number_abs ?: 0,
                episode.overview ?: "",
                episode.first_aired,
                episode.updated_at.toString(),
                episode.rating?.toFloat() ?: 0f,
                episode.votes ?: 0,
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

    private fun getReadableAirsIn(airingTime: AiringTime): String {
        val nextAirDateTime = getAirDateTimeInCurrentTimeZone(LocalDateTime.now(),airingTime)
            ?.toOffsetDateTime()
        if (nextAirDateTime != null) {
            val diffInDays = nextAirDateTime.diffInDays()
            return if (diffInDays > 0) {
                "in $diffInDays days"
            } else {
                val diffInHours = nextAirDateTime.diffInHours()
                if (diffInHours > 0) {
                    "in $diffInHours hours"
                } else {
                    "in less then an hour"
                }
            }
        }
        return ""
    }
}