package hu.csabapap.seriesreminder.data.repositories.episodes

import hu.csabapap.seriesreminder.data.db.daos.EpisodeDao
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import javax.inject.Inject

class LocalEpisodesDataSource @Inject constructor(private val episodesDao: EpisodeDao) {

    fun save(episode: SREpisode) {
        episodesDao.insert(episode)
    }

}