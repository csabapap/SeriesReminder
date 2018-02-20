package hu.csabapap.seriesreminder

import hu.csabapap.seriesreminder.data.db.daos.NextEpisodeDao
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import javax.inject.Inject

class EpisodesRepository @Inject constructor(
        private val traktApi: TraktApi,
        private val tvdbApi: TvdbApi,
        private val nextEpisodeDao: NextEpisodeDao) {

    fun insertNextEpisode(nextEpisodeEntry: NextEpisodeEntry) {
        nextEpisodeDao.insert(nextEpisodeEntry)
    }


}