package hu.csabapap.seriesreminder.data.repositories.relatedshows

import androidx.lifecycle.LiveData
import hu.csabapap.seriesreminder.data.db.daos.RelatedShowsDao
import hu.csabapap.seriesreminder.data.db.entities.RelatedShow
import hu.csabapap.seriesreminder.data.db.relations.RelatedShowWithShow
import javax.inject.Inject

class LocalRelatedShowsDataSource @Inject constructor(private val dao: RelatedShowsDao) {

    fun save(relatedShows: List<RelatedShow>) {
        dao.insert(relatedShows)
    }

    fun liveEntries(showId: Int): LiveData<List<RelatedShowWithShow>> {
        return dao.relatedLiveEntries(showId)
    }
}