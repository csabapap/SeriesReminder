package hu.csabapap.seriesreminder.data.repositories.nextepisodes

import hu.csabapap.seriesreminder.data.db.daos.NextEpisodeDao
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry
import javax.inject.Inject

class NextEpisodesLocalDataSource @Inject constructor(
        private val nextEpisodeDao: NextEpisodeDao) {

    fun saveNextEpisode(nextEpisodeEntry: NextEpisodeEntry) {
        nextEpisodeDao.insert(nextEpisodeEntry)
    }
}