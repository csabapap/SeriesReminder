package hu.csabapap.seriesreminder.data.repositories.episodes

import hu.csabapap.seriesreminder.data.db.daos.EpisodeDao
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import javax.inject.Inject

class LocalEpisodesDataSource @Inject constructor(private val episodesDao: EpisodeDao) {

    fun save(episode: SREpisode) {
        episodesDao.upsert(episode)
    }

    fun save(episodes: List<SREpisode>) {
        episodesDao.upsert(episodes)
    }

    fun saveImage(tvdbId: Int, url: String) {
        episodesDao.updateEpisodeWithImage(tvdbId, url)
    }

    suspend fun get(showId: Int, absNumber: Int): SREpisode? {
        return episodesDao.getByAbsNumber(showId, absNumber)
    }

    suspend fun getAllEpisodesForShow(showId: Int): List<SREpisode> {
        return episodesDao.getAllForShow(showId)
    }

    suspend fun getBySeasonAndEpisodeNumber(showId: Int, season: Int, episode: Int): EpisodeWithShow? {
        return episodesDao.getBySeasonAndEpisodeNumber(showId, season, episode)
    }

    suspend fun getEpisodesForSeason(showId: Int, seasonNumber: Int) =
            episodesDao.getEpisodesBySeason(showId, seasonNumber)

    fun getUpcomingEpisodes(limit: Int = 3) = episodesDao.getUpcomingEpisodes(limit)
}