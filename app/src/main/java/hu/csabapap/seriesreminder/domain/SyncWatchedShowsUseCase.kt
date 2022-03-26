package hu.csabapap.seriesreminder.domain

import com.uwetrottmann.trakt5.entities.UserSlug
import com.uwetrottmann.trakt5.enums.Extended
import com.uwetrottmann.trakt5.services.Users
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.repositories.loggedinuser.LoggedInUserRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.utils.safeApiCall
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.OffsetDateTime
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class SyncWatchedShowsUseCase @Inject constructor(
        private val loggedInUserRepository: LoggedInUserRepository,
        private val collectionRepository: CollectionRepository,
        private val traktUsers: Users,
        private val getWatchedShowUseCase: GetWatchedShowUseCase,
        private val showsRepository: ShowsRepository,
        private val seasonsRepository: SeasonsRepository
) {
    suspend fun sync() {
        val start = System.currentTimeMillis()
        if (!loggedInUserRepository.isLoggedIn()) return

        val result = watchedShowsFromTrakt()
        if (result is Result.Error) {
            Timber.e(result.exception)
            return
        }
        if (result is Result.Success) {
            val data = result.data
            coroutineScope {
                val watchedShowsFromTratk = data.map {
                    async {
                        val id = it.show?.ids?.trakt ?: return@async null
                        getWatchedShowUseCase(id)
                    }
                }.awaitAll()

                val shows = watchedShowsFromTratk.mapNotNull { it?.show }
                showsRepository.insertShows(shows)
                val collectionEntries = shows.map {
                    CollectionEntry(showId = it.traktId, added = OffsetDateTime.now())
                }
                collectionRepository.saveAll(collectionEntries)

                val seasonsFromWeb = watchedShowsFromTratk.mapNotNull { it?.seasonsWithImages }
                val allSeasonsToSave = seasonsFromWeb.map { remoteSeasonWithImages ->
                    val localSeasonsMap = seasonsRepository.getSeasonsFromDb(remoteSeasonWithImages.showTraktId)
                            ?.associateBy { remoteSeasonWithImages.showTraktId } ?: emptyMap()
                    remoteSeasonWithImages.seasons.map { season ->
                        val localSeason = localSeasonsMap[remoteSeasonWithImages.showTraktId]
                        val image = remoteSeasonWithImages.seasonsImages[season.number.toString()]
                        season.copy(id = localSeason?.id, fileName = image?.fileName ?: "", thumbnail = image?.thumbnail
                                ?: "", nmbOfWatchedEpisodes = localSeason?.nmbOfWatchedEpisodes ?: 0)
                    }
                }.flatten()
                seasonsRepository.upsertSeasons(allSeasonsToSave)
                Timber.d("nmb of watched shows from trakt (${watchedShowsFromTratk.size}) inserted in: ${System.currentTimeMillis() - start}ms")
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