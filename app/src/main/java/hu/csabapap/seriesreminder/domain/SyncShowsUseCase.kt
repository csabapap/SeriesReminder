package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import javax.inject.Inject

class SyncShowsUseCase @Inject constructor(val showsRepository: ShowsRepository,
                                           val seasonsRepository: SeasonsRepository,
                                           val collectionRepository: CollectionRepository) {


    suspend fun syncShows() {
        Timber.d("sync shows")
        coroutineScope {
            val collectionItems = collectionRepository.getCollectionsSuspendable()

            collectionItems.map {collectionItem ->
                val show = collectionItem.show ?: return@map null
                async {
                    showsRepository.getShowWithImages(show.traktId, show.tvdbId).await()

                    seasonsRepository.syncSeasons(show.traktId)

                }
            }
                    .filterNotNull()
                    .awaitAll()

        }
    }

}