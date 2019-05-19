package hu.csabapap.seriesreminder.data.repositories.relatedshows

import hu.csabapap.seriesreminder.data.db.daos.RelatedShowsDao
import hu.csabapap.seriesreminder.data.db.entities.RelatedShow
import javax.inject.Inject

class LocalRelatedShowsDataSource @Inject constructor(private val dao: RelatedShowsDao) {

    fun save(relatedShows: List<RelatedShow>) {
        dao.insert(relatedShows)
    }

}