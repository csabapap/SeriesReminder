package hu.csabapap.seriesreminder.data.repositories.shows

import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.entities.AiringTime
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.utils.safeApiCall
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowsRepository @Inject constructor(private val traktApi: TraktApi,
                                          private val tvdbApi: TvdbApi,
                                          private val showDao: SRShowDao,
                                          private val remoteDataSource: RemoteDataSource
){
    suspend fun getShow(traktId: Int): SRShow? {
        val showFromDb = showDao.getShow(traktId)
        if (showFromDb != null) return showFromDb
        val showResult = remoteDataSource.show(traktId)
        if (showResult is Result.Success) {
            return mapToSRShow(showResult.data)
        }
        return null
    }

    suspend fun getShowWithImages(traktId: Int, tvdbId: Int): SRShow? {
        val showFromDb = showDao.getShow(traktId)
        if (showFromDb != null) {
            return showFromDb
        }
        val showResult = getShowFromWebWithImages(traktId, tvdbId)

        if (showResult is Result.Success) {
            val show = showResult.data
            insertOrUpdateShow(show)
            return show
        }
        return null
    }

    fun insertShow(show: SRShow) {
        showDao.insertOrUpdateShow(show)
    }

    private fun insertOrUpdateShow(show: SRShow): SRShow {
        Timber.d("insert or update show: %d", show.traktId)
        return showDao.insertOrUpdateShow(show)
    }

    private suspend fun getShowFromWebWithImages(traktId: Int, tvdbId: Int): Result<SRShow> {
        return safeApiCall({
            val show = traktApi.show(traktId).body()!!
            val images = tvdbApi.images(tvdbId)
            val bestVotedImage = images?.data?.maxBy { image ->
                image.ratingsInfo.average
            }
            return@safeApiCall Result.Success(mapToSRShow(show, bestVotedImage))
        }, "error during get show with images, trakt id: $traktId, tvdb id: $tvdbId")
    }

    private fun mapToSRShow(show : Show, images: Image? = null) : SRShow {
        val srShow = SRShow()
        srShow.apply {
            updateProperty(this::traktId, show.ids.trakt)
            updateProperty(this::tvdbId, show.ids.tvdb)
            updateProperty(this::title, show.title)
            updateProperty(this::overview, show.overview)
            updateProperty(this::rating, show.rating)
            updateProperty(this::votes, show.votes)
            updateProperty(this::genres, show.genres.joinToString())
            updateProperty(this::runtime, show.runtime)
            updateProperty(this::airedEpisodes, show.aired_episodes)
            updateProperty(this::status, show.status)
            updateProperty(this::network, show.network)
            updateProperty(this::trailer, show.trailer ?: "")
            updateProperty(this::homepage, show.homepage ?: "")
            updateProperty(this::updatedAt, OffsetDateTime.parse(show.updated_at))
            show.airs?.let {
                val updateVal = AiringTime(it.day,
                        it.time, it.timezone)
                updateProperty(this::airingTime, updateVal)
            }
            images?.let {
                updateProperty(this::posterThumb, it.thumbnail)
                updateProperty(this::poster, it.fileName)
            }
        }
        return srShow
    }

    suspend fun updateNextEpisode(showId: Int, nextEpisodeNumber: Int) {
        showDao.updateNextEpisode(showId, nextEpisodeNumber)
    }
}