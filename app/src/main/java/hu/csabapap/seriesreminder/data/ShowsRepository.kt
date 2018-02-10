package hu.csabapap.seriesreminder.data

import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.db.entities.*
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.data.network.entities.Images
import hu.csabapap.seriesreminder.data.network.entities.Show
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber

class ShowsRepository(private val traktApi: TraktApi, private val tvdbApi: TvdbApi,
                      private val showDao: SRShowDao, private val trendingDao: TrendingDao,
                      private val popularDao: PopularDao){

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
                                    val popularImage = data.maxBy { it.ratings.average }
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
            updateProperty(this::poster, show.image)
            updateProperty(this::posterThumb, show.thumb)
            updateProperty(this::genres, show.genres.joinToString())
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
}