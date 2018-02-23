package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.db.entities.*
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Images
import hu.csabapap.seriesreminder.data.network.entities.NextEpisode
import hu.csabapap.seriesreminder.data.network.entities.Show
import hu.csabapap.seriesreminder.data.states.NextEpisodeError
import hu.csabapap.seriesreminder.data.states.NextEpisodeState
import hu.csabapap.seriesreminder.data.states.NextEpisodeSuccess
import hu.csabapap.seriesreminder.data.states.NoNextEpisode
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.threeten.bp.OffsetDateTime
import timber.log.Timber

class ShowsRepository(private val traktApi: TraktApi, private val tvdbApi: TvdbApi,
                      private val showDao: SRShowDao, private val trendingDao: TrendingDao,
                      private val popularDao: PopularDao,
                      private val episodesRepository: EpisodesRepository){

    var cachedTrendingShows: MutableList<SRShow> = mutableListOf()

    fun getTrendingShows(limit: Int = 10) : Flowable<List<TrendingGridItem>> {
        return trendingDao.getTrendingShows(limit)
    }

    fun getRemoteTrendingShows(): Single<List<SRTrendingItem>> {
        return traktApi.trendingShows("full")
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe {
                    getShow(it.show.ids.trakt)
                            .map { showDao.insertOrUpdateShow(it) }
                            .map { showDao.getShow(it.traktId)}
                            .map { srShow -> mapToSRTrendingShow(srShow.traktId, it.watchers) }
                }
                .toList()
                .doOnSuccess {
                    trendingDao.deleteAll()
                    saveTrendingShows(it)
                }
                .toObservable()
                .flatMapIterable { it }
                .flatMap {
                    updateShowWithImages(it.showId)
                            .toObservable()
                            .map { srShow ->  mapToSRTrendingShow(srShow.traktId, it.watchers) }
                }
                .toList()
    }

    fun popularShows(limit: Int = 10) : Flowable<List<PopularGridItem>> {
        return popularDao.getPopularShows(limit)
    }

    fun getPopularShowsFromWeb(limit: Int = 20) : Single<List<SRPopularItem>> {
        return traktApi.popularShows(limit)
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe {
                    getShow(it.ids.trakt)
                            .map { showDao.insertOrUpdateShow(it) }
                            .map { showDao.getShow(it.traktId)}
                            .map { srShow -> mapToSRPopularItem(srShow.traktId) }
                }
                .toList()
                .doOnSuccess {
                    popularDao.deleteAll()
                    savePopularItem(it)
                }
                .toObservable()
                .flatMapIterable { it }
                .flatMap {
                    updateShowWithImages(it.showId)
                            .toObservable()
                            .map { srShow ->  mapToSRPopularItem(srShow.traktId) }
                }
                .toList()

    }

    fun getShow(traktId: Int) : Maybe<SRShow> {
        val showFromDb = showDao.getShowMaybe(traktId)

        val fromWeb = getShowFromWeb(traktId).singleElement()

        return Maybe.concat(showFromDb, fromWeb).firstElement()
    }

    private fun getShowFromWeb(traktId: Int) : Flowable<SRShow>{
        return traktApi.show(traktId)
                .flatMap({
                    Flowable.just(mapToSRShow(it))
                })
    }

    fun images(tvdbId : Int, type: String = "poster") : Single<Images>{
        return tvdbApi.images(tvdbId, type)
    }

    private fun updateShowWithImages(showId: Int) : Single<SRShow> {
        Timber.d("$showId")
        return getShow(showId)
                .flatMapSingle{
                    if (it.poster.isEmpty()) {
                        Timber.d("get images for $showId")
                        tvdbApi.images(it.tvdbId)
                                .flatMap { (data) ->
                                    val popularImage = data.maxBy { image ->
                                        Timber.d("image: $image")
                                        image.ratingsInfo.average
                                    }
                                    it.apply {
                                        poster = popularImage?.fileName!!
                                        posterThumb = popularImage.thumbnail
                                    }
                                    updateShow(it)
                                    Single.just(it)
                                }
                    } else {
                        Single.just(it)
                    }
                }

    }

    private fun mapToSRShow(show : Show) : SRShow {
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
                updateProperty(this::airingTime, AiringTime(it.day,
                        it.time,it.timezone))
            }
        }
        return srShow
    }

    private fun mapToSRTrendingShow(showId: Int, watchers: Int) : SRTrendingItem =
            SRTrendingItem(null, showId, watchers)

    private fun mapToSRPopularItem(showId: Int) : SRPopularItem = SRPopularItem(showId = showId)


    private fun saveTrendingShows(trendingItems: List<SRTrendingItem>) {
        trendingItems.forEach { trendingShow ->
            trendingDao.insert(trendingShow)
        }
    }

    private fun savePopularItem(popularItems: List<SRPopularItem>) {
        popularItems.forEach {
            popularDao.insert(it)
        }
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
                .doAfterSuccess({
                    if (it is NextEpisodeSuccess) {
                        saveNextEpisode(it.nextEpisode)
                    }
                })
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
}