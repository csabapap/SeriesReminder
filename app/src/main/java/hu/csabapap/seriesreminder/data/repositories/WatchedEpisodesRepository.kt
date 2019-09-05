package hu.csabapap.seriesreminder.data.repositories

import hu.csabapap.seriesreminder.data.db.daos.WatchedEpisodesDao
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithWatchedInfo
import javax.inject.Inject

class WatchedEpisodesRepository @Inject constructor(private val watchedEpisodesDao: WatchedEpisodesDao) {

    suspend fun setEpisodeWatched(watchedEpisode: WatchedEpisode) =
            watchedEpisodesDao.insert(watchedEpisode)

    suspend fun getEpisodesWithWatchedData(showId: Int, seasonNumber: Int) =
            watchedEpisodesDao.getEpisodesWithWatchedInfo(showId, seasonNumber)

}