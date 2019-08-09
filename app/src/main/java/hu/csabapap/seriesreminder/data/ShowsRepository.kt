package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.LastRequestDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.entities.*
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.data.network.entities.Show
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowsRepository @Inject constructor(private val traktApi: TraktApi,
                                          private val tvdbApi: TvdbApi,
                                          private val showDao: SRShowDao,
                                          private val requestDao: LastRequestDao,
                                          private val collectionRepository: CollectionRepository){

    fun getShow(traktId: Int) : Maybe<SRShow> {
        val showFromDb = showDao.getShowMaybe(traktId)

        val fromWeb = getShowFromWeb(traktId).singleElement()

        return Maybe.concat(showFromDb, fromWeb).firstElement()
    }

    fun getShowWithImages(traktId: Int, tvdbId: Int): Maybe<SRShow> {
        val showFromDb = showDao.getShowMaybe(traktId)

        val fromWeb = Maybe.fromCallable {
            val lastRequest = requestDao.getLastRequestById(traktId, Request.SHOW_DETAILS)
            if (requestDao.isRequestBefore(lastRequest, Duration.ofHours(4)))
                Maybe.just(true)
            else
                Maybe.empty()
        }.flatMap {
            requestDao.insert(LastRequest(0L, traktId,
                    Request.SHOW_DETAILS, Instant.now()))
            getShowFromWebWithImages(traktId, tvdbId).toMaybe()
                    .doOnSuccess { show ->
                        insertOrUpdateShow(show)
                    }
        }

        return Maybe.concat(showFromDb, fromWeb).firstElement()
    }

    fun insertShow(show: SRShow) {
        showDao.insertOrUpdateShow(show)
    }

    private fun insertOrUpdateShow(show: SRShow): SRShow {
        Timber.d("insert or update show: %d", show.traktId)
        return showDao.insertOrUpdateShow(show)
    }

    private fun getShowFromWeb(traktId: Int) : Flowable<SRShow>{
        return traktApi.show(traktId)
                .toFlowable()
                .flatMap {
                    Flowable.just(mapToSRShow(it))
                }
    }

    private fun getShowFromWebWithImages(traktId: Int, tvdbId: Int): Single<SRShow> {
        val showSingle = traktApi.show(traktId)
        val imagesSingle = tvdbApi.imagesSingle(tvdbId)
                .map {
                    it.data.maxBy { image ->
                        image.ratingsInfo.average
                    }
                }
        return showSingle.zipWith(imagesSingle, BiFunction { show, image ->
            mapToSRShow(show, image)
        })
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

//    fun syncShows(): Single<MutableList<SRShow>> {
//        return collectionRepository.getCollectionsSuspendable()
//                .flattenAsFlowable { it }
//                .filter { it.show != null }
//                .flatMap { Flowable.just(it.show) }
//                .flatMap {
//                    traktApi.show(it.traktId)
//                            .map { show -> mapToSRShow(show) }
//                            .toFlowable()
//                }
//                .flatMap {
//                    Timber.d("update show: %s", it.title)
//                    showDao.updateShow(it)
//                    Flowable.just(it)
//                }
//                .toList()
//    }

    suspend fun updateNextEpisode(showId: Int, nextEpisodeNumber: Int) {
        showDao.updateNextEpisode(showId, nextEpisodeNumber)
    }
}