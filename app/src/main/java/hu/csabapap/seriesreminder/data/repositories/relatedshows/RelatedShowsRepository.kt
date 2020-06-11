package hu.csabapap.seriesreminder.data.repositories.relatedshows

import androidx.lifecycle.LiveData
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.RelatedShow
import hu.csabapap.seriesreminder.data.db.relations.RelatedShowWithShow
import timber.log.Timber
import javax.inject.Inject

class RelatedShowsRepository @Inject constructor(
        private val localDataSource: LocalRelatedShowsDataSource,
        private val remoteDataSource: RemoteRelatedShowsDataSource,
        private val showsRepository: ShowsRepository) {

    suspend fun refreshRelatedShows(id: Int) {
        val remoteRelatedShows = remoteDataSource.relatedShows(id).await()

        val relatedShows = mutableListOf<RelatedShow>()
        for (remoteRelatedShow in remoteRelatedShows) {
            try {
                val show = showsRepository.getShowWithImages(remoteRelatedShow.ids.trakt, remoteRelatedShow.ids.tvdb)
                show?.let {
                    showsRepository.insertShow(it)
                }
                relatedShows.add(RelatedShow(relatedId = remoteRelatedShow.ids.trakt, relatesTo = id))
            } catch (e: Throwable) {
                Timber.e(e)
            }
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