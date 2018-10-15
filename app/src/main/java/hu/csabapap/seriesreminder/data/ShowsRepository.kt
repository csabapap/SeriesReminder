package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.entities.AiringTime
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Image
import hu.csabapap.seriesreminder.data.network.entities.NextEpisode
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.states.NextEpisodeError
import hu.csabapap.seriesreminder.data.states.NextEpisodeState
import hu.csabapap.seriesreminder.data.states.NextEpisodeSuccess
import hu.csabapap.seriesreminder.data.states.NoNextEpisode
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowsRepository @Inject constructor(private val traktApi: TraktApi,
                                          private val tvdbApi: TvdbApi,
                                          private val showDao: SRShowDao,
                                          private val seasonsRepository: SeasonsRepository,
                                          private val episodesRepository: EpisodesRepository,
                                          private val collectionRepository: CollectionRepository){

    fun getShow(traktId: Int) : Maybe<SRShow> {
        val showFromDb = showDao.getShowMaybe(traktId)

        val fromWeb = getShowFromWeb(traktId).singleElement()

        return Maybe.concat(showFromDb, fromWeb).firstElement()
    }

    fun getShowWithImages(traktId: Int, tvdbId: Int): Maybe<SRShow> {
        val showFromDb = showDao.getShowMaybe(traktId)

        val fromWeb = getShowFromWebWithImages(traktId, tvdbId).toMaybe()

        return Maybe.concat(showFromDb, fromWeb).firstElement()
    }

    fun insertShow(show: SRShow) {
        showDao.insertOrUpdateShow(show)
    }

    fun insertOrUpdateShow(show: SRShow) = showDao.insertOrUpdateShow(show)

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

    fun updateShow(show: SRShow) {
        showDao.updateShow(show)
    }

    fun fetchNextEpisode(showId: Int): Single<NextEpisodeState> {
        return traktApi.nextEpisode(showId)
                .flatMap {
                    when (it.code()) {
                        200 -> Single.just(NextEpisodeSuccess(mapToNextEpisodeEntry(it.body()!!, showId)))
                        204 -> Single.just(NoNextEpisode)
                        else -> Single.just(NextEpisodeError("error during next episode fetching"))
                    }
                }
                .doAfterSuccess {
                    if (it is NextEpisodeSuccess) {
                        saveNextEpisode(it.nextEpisode)
                    }
                }
    }

    private fun mapToNextEpisodeEntry(nextEpisode: NextEpisode, showId: Int): NextEpisodeEntry {
        return NextEpisodeEntry(null,
                nextEpisode.season,
                nextEpisode.number,
                nextEpisode.title,
                nextEpisode.ids.trakt,
                nextEpisode.ids.tvdb,
                showId)
    }

    private fun saveNextEpisode(nextEpisodeEntry: NextEpisodeEntry) {
        episodesRepository.insertNextEpisode(nextEpisodeEntry)
    }

    fun getSeasons(showId: Int): Single<List<SRSeason>> {
        return seasonsRepository.getSeasons(showId)
    }

    fun syncShows(): Single<MutableList<SRShow>> {
        return collectionRepository.getCollectionsSingle()
                .flattenAsFlowable { it }
                .filter { it.show != null }
                .flatMap { Flowable.just(it.show) }
                .flatMap {
                    traktApi.show(it.traktId)
                            .map { show -> mapToSRShow(show) }
                            .toFlowable()
                }
                .flatMap {
                    Timber.d("update show: %s", it.title)
                    showDao.updateShow(it)
                    Flowable.just(it)
                }
                .toList()
    }
}