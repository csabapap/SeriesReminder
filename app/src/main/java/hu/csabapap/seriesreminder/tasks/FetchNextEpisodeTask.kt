package hu.csabapap.seriesreminder.tasks

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.states.NextEpisodeSuccess
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import javax.inject.Inject

class FetchNextEpisodeTask(val showId: Int): Task {

    @Inject
    lateinit var showsRepository: ShowsRepository

    @Inject
    lateinit var collectionRepository: CollectionRepository

    override suspend fun execute() {

        val collectionEntry = collectionRepository.getCollectionItem(showId).await() ?: return

        if (collectionEntry.id == null) return

        val nextEpisodeState = showsRepository.fetchNextEpisode(showId)
        if (nextEpisodeState is NextEpisodeSuccess) {
            val nextEpisode =nextEpisodeState.nextEpisode
            val entry = showsRepository.mapToNextEpisodeEntry(nextEpisode, showId, collectionEntry.id.toInt())
            showsRepository.saveNextEpisode(entry)
            Timber.d("next episode: $entry")
        }
    }
}