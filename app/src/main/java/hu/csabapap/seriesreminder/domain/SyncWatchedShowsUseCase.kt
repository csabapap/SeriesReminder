package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.network.services.UsersService
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.utils.safeApiCall
import timber.log.Timber
import javax.inject.Inject

class SyncWatchedShowsUseCase @Inject constructor(
        private val loggedInUserRepository: LoggedInUserRepository,
        private val collectionRepository: CollectionRepository,
        private val showsRepository: ShowsRepository,
        private val usersService: UsersService
) {
    suspend fun sync() {
        if (!loggedInUserRepository.isLoggedIn()) return

        val result = watchedShowsFromTrakt()
        if (result is Result.Error) {
            Timber.e(result.exception)
            return
        }
        if (result is Result.Success) {
            val data = result.data
            val collectionMap = collectionRepository.getCollectionsSuspendable().associateBy { it.entry?.showId }
            data.forEach {
                if (!collectionMap.containsKey(it.show.ids.trakt)){
                    Timber.d("${it.show.title} watched show is not in collection")
                } else {
                    Timber.d("${it.show.title} watched show is in collection")
                }
            }
        }


    }

    suspend fun watchedShowsFromTrakt() = safeApiCall({
        val result = usersService.watchedShows()
        return@safeApiCall Result.Success(result)
    }, "error during getting watched shows")
}