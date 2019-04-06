package hu.csabapap.seriesreminder.tasks

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.repositories.nextepisodes.NextEpisodesRepository
import hu.csabapap.seriesreminder.data.states.NextEpisodeSuccess
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import javax.inject.Inject

class FetchNextEpisodeTask(val showId: Int) : Task {

    @Inject
    lateinit var nextEpisodeRepository: NextEpisodesRepository

    @Inject
    lateinit var collectionRepository: CollectionRepository

    override suspend fun execute() {

        val collectionEntry = collectionRepository.getCollectionItem(showId).await() ?: return

        if (collectionEntry.id == null) return

        nextEpisodeRepository.fetchNextEpisode(showId)
    }
}