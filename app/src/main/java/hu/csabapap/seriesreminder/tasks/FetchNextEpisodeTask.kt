package hu.csabapap.seriesreminder.tasks

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.domain.GetNextEpisodeUseCase
import javax.inject.Inject

class FetchNextEpisodeTask(val showId: Int) : Task {

    @Inject
    lateinit var nextEpisodeUseCase: GetNextEpisodeUseCase

    @Inject
    lateinit var collectionRepository: CollectionRepository

    override suspend fun execute() {

        val collectionEntry = collectionRepository.getCollectionItem(showId)

        if (collectionEntry.id == null) return

        nextEpisodeUseCase.getNextEpisode(showId)
    }
}