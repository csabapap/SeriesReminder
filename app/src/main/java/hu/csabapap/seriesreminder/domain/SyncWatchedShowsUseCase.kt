package hu.csabapap.seriesreminder.domain

import com.uwetrottmann.trakt5.entities.UserSlug
import com.uwetrottmann.trakt5.enums.Extended
import com.uwetrottmann.trakt5.services.Users
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import hu.csabapap.seriesreminder.utils.safeApiCall
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class SyncWatchedShowsUseCase @Inject constructor(
        private val loggedInUserRepository: LoggedInUserRepository,
        private val collectionRepository: CollectionRepository,
        private val traktUsers: Users,
        private val addShowToCollectionUseCase: AddShowToCollectionUseCase
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
                val show = it.show ?: return@forEach
                val traktId = show.ids?.trakt ?: return@forEach
                if (!collectionMap.containsKey(traktId)){
                    Timber.d("${show.title} watched show is not in collection")
                    addShowToCollectionUseCase.addShow(traktId)
                } else {
                    Timber.d("${show.title} watched show is in collection")
                }
            }
        }
    }

    suspend fun watchedShowsFromTrakt() = safeApiCall({
        val response = traktUsers.watchedShows(UserSlug.ME, Extended.NOSEASONS).execute()
        if (response.isSuccessful) {
            return@safeApiCall Result.Success(response.body() ?: emptyList())
        }
        return@safeApiCall Result.Error(HttpException(response))
    }, "error during getting watched shows")
}