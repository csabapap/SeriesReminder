package hu.csabapap.seriesreminder.data.repositories.relatedshows

import androidx.lifecycle.LiveData
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.RelatedShow
import hu.csabapap.seriesreminder.data.db.relations.RelatedShowWithShow
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class RelatedShowsRepository @Inject constructor(
        private val localDataSource: LocalRelatedShowsDataSource,
        private val remoteDataSource: RemoteRelatedShowsDataSource,
        private val showsRepository: ShowsRepository) {

    suspend fun refreshRelatedShows(id: Int) {
        val remoteRelatedShows = remoteDataSource.relatedShows(id).await()

        for (remoteRelatedShow in remoteRelatedShows) {
            val show = showsRepository.getShowWithImages(remoteRelatedShow.ids.trakt, remoteRelatedShow.ids.tvdb).await()
            show?.let {
                showsRepository.insertShow(it)
            }
        }

        val relatedShows = remoteRelatedShows.map {
            RelatedShow(relatedId = it.ids.trakt, relatesTo = id)
        }

        saveRelatedShows(relatedShows)
    }

    private fun saveRelatedShows(relatedShows: List<RelatedShow>) {
        localDataSource.save(relatedShows)
    }

    fun liveRelatedShows(showId: Int): LiveData<List<RelatedShowWithShow>> {
        return localDataSource.liveEntries(showId)
    }
}